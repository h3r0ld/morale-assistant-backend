package hu.herolds.projects.morale.service

import groovy.util.logging.Slf4j
import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.controller.dto.mapToJokeDto
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.exception.ResourceNotFoundException
import hu.herolds.projects.morale.repository.JokeRepository
import hu.herolds.projects.morale.service.sounds.SoundStorage
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

const val GENERAL_JOKE_TEXT = "You know what's a complete joke? This site, and when it can't find the next joke for you! :("

@Service
@Transactional
class JokeCacheService(
    private val jokeRepository: JokeRepository,
    private val synthesizerService: SynthesizerService,
    private val soundStorage: SoundStorage,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Cacheable("jokes")
    fun getJoke(id: UUID): JokeDto {
        log.info("Get joke with id [$id]")
        val joke = getJokeById(id)
        return joke.mapToJokeDto().apply {
            soundFile = joke.soundFilePath?.let {
                soundStorage.load(joke)
            }
        }
    }

    @Cacheable("generalJoke")
    fun getGeneralJoke(): JokeDto {
        log.info("Get general joke")
        return JokeDto(
            language = Language.EN,
            text = GENERAL_JOKE_TEXT,
        ).apply {
            soundFile = synthesizerService.synthesize(this.language, this.text)
        }
    }

    private fun getJokeById(id: UUID): Joke = jokeRepository.findByIdOrNull(id)
        ?: throw ResourceNotFoundException(id = id, message = "Joke not found with id: [$id]")
}