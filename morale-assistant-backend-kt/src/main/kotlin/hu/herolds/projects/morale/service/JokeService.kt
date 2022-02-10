package hu.herolds.projects.morale.service

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.controller.dto.mapToJokeDto
import hu.herolds.projects.morale.controller.dto.paging.JokeSearchRequest
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.Joke_
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.exception.GetRandomJokeException
import hu.herolds.projects.morale.exception.ResourceNotFoundException
import hu.herolds.projects.morale.exception.SoundFileNotFoundException
import hu.herolds.projects.morale.repository.JokeRepository
import hu.herolds.projects.morale.util.and
import hu.herolds.projects.morale.util.likeIgnoreCase
import hu.herolds.projects.morale.util.toByteArray
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.*
import javax.persistence.criteria.Predicate

const val GENERAL_JOKE_TEXT = "You know what's a complete joke? This site, and when it can't find the next joke for you! :("

@Service
@Transactional
class JokeService(
    private val jokeRepository: JokeRepository,
    private val synthesizerService: SynthesizerService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun searchJokes(request: JokeSearchRequest): Page<JokeDto> {
        return jokeRepository.findAll({ root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            if (!request.text.isNullOrBlank()) {
                predicates.add(cb.likeIgnoreCase(path = root.get(Joke_.text), value = request.text))
            }
            if (request.language != null) {
                predicates.add(cb.equal(root.get(Joke_.language), request.language))
            }

            cb.and(predicates)
        }, request.toPageRequest()).map(Joke::mapToJokeDto)
    }

    @Transactional
    fun saveJoke(jokeDto: JokeDto): UUID = jokeRepository.save(
        Joke(
            language = jokeDto.language,
            text = jokeDto.text,
            soundFilePath = synthesizerService.synthesize(jokeDto.language, jokeDto.text).toUri()
        )
    ).also {
        log.info("Saved new joke: [${it.id}]")
    }.id!!

    fun updateJoke(id: UUID, jokeDto: JokeDto) {
        updateJoke(getJokeById(id), jokeDto)
        log.info("Updated joke (id: [$id])")
    }

    @Retryable(
            include = [SoundFileNotFoundException::class], maxAttempts = 1,
            exclude = [ResourceNotFoundException::class])
    fun getJoke(id: UUID): JokeDto {
        return getJokeById(id).mapToJokeDto(withSoundFile = true)
    }

    fun deleteJoke(id: UUID) {
        val joke = getJokeById(id)

        joke.soundFilePath?.let { uri ->
            val soundFile = File(uri.path)
            if (soundFile.exists()) {
                val success = soundFile.delete()
                if (success) {
                    log.info("Joke(id:$id) audio file successfully deleted: [$uri]")
                } else {
                    log.error("Joke(id:$id) failed to delete audio file: [$uri]")
                }
            } else {
                log.error("Joke(id: $id) audio file was not found: [$uri]")
            }
        }

        jokeRepository.delete(joke)
        log.info("Joke(id: $id) was deleted successfully.")
    }

    @Retryable(
        value = [GetRandomJokeException::class],
        maxAttemptsExpression = "\${randomJoke.maxAttempts}",
    )
    fun getRandomJoke(language: Language): JokeDto {
        log.info("Getting a random joke with language: [$language]")

        val count = jokeRepository.countByLanguage(language)
        log.debug("[$language] Joke count: [$count]")
        val jokeIndex = (Math.random() * count).toInt()
        log.debug("Random page index: [$jokeIndex]")

        val singleJokePage = jokeRepository.findByLanguageAndSoundFilePathNotNull(language, PageRequest.of(jokeIndex, 1))
        if (singleJokePage.hasContent()) {
            val joke = singleJokePage.content[0]
            log.info("Found a random joke(id: [${joke.id}])")
            return joke.mapToJokeDto(withSoundFile = true)
        } else {
            log.warn("Could not get a random joke with page index: [$jokeIndex]")
            throw GetRandomJokeException("Could not find the next joke!")
        }
    }

    @Recover
    fun getGeneralJoke(exception: GetRandomJokeException, language: Language): JokeDto {
        log.error("Could not get [$language] random joke! Returning general joke.")

        return JokeDto(
            language = Language.EN,
            text = GENERAL_JOKE_TEXT,
        ).apply {
            soundFile = synthesizerService.synthesize(this.language, this.text).toUri().toByteArray()
        }
    }

    @Recover
    fun handleSoundFileNotFound(exception: SoundFileNotFoundException): JokeDto = exception.joke.let {
        updateJoke(it, JokeDto(text = it.text, language = it.language)).mapToJokeDto(withSoundFile = true)
    }

    @Recover
    fun handleResourceNotFound(exception: ResourceNotFoundException): JokeDto {
        throw exception
    }

    private fun getJokeById(id: UUID): Joke = jokeRepository.findByIdOrNull(id)
        ?: throw ResourceNotFoundException(id = id, message = "Joke not found with id: [$id]")

    private fun updateJoke(joke: Joke, jokeDto: JokeDto): Joke =
        joke.apply {
                language = jokeDto.language
                text = jokeDto.text
                soundFilePath = synthesizerService.synthesize(jokeDto.language, jokeDto.text).toUri()
        }.let {
            jokeRepository.save(it)
        }
}
