package hu.herolds.projects.morale.service

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.repository.JokeRepository
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class JokeService(
    private val synthesizerService: SynthesizerService,
    private val jokeRepository: JokeRepository
) {

    fun getJokes() = jokeRepository.findAll().map(this::mapToJokeDto)

    fun saveJoke(jokeDto: JokeDto) {
        val joke: Joke = jokeDto.let {
            Joke(
                language = it.language,
                text = it.text
            )
        }
        jokeRepository.save(joke)
    }

    fun getRandomJoke(language: Language): JokeDto {
        val jokes = jokeRepository.findAll()
        val nextJokeIndex = Random().nextInt(jokes.size)
        return mapToJokeDto(jokes[nextJokeIndex])
    }

    private fun mapToJokeDto(joke: Joke): JokeDto {
        val soundFile: ByteArray? = try {
            IOUtils.toByteArray(joke.soundFilePath)
        } catch (e: IOException) {
            log.error("Could not load sound file for joke.", e)
            null
        }

        return JokeDto(
            id = joke.id,
            language = joke.language,
            text = joke.text,
            path = joke.soundFilePath.toString(),
            soundFile = soundFile
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(JokeService::class.java)
    }
}