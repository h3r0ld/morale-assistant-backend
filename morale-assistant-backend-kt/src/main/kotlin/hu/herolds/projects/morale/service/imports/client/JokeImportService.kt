package hu.herolds.projects.morale.service.imports.client

import hu.herolds.projects.morale.controller.dto.AvailableJokeSource
import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.service.JokeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch

@Service
class JokeImportService(
    apiClients: List<JokeApiService>,
    private val jokeService: JokeService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val apiClients = apiClients.associateBy { it.source }

    fun importJokes(source: AvailableJokeSource, count: Int) {
        val stopwatch = StopWatch().apply {
            start()
        }

        val apiClient = apiClients[source]

        for (i in 1..count) {
            log.info("Importing joke [$i/$count]")
            jokeService.saveJoke(jokeDto = JokeDto(
                language = Language.EN,
                text = apiClient!!.getRandomJoke().value
            ))
        }

        stopwatch.stop()
        log.info("Imported [$count] jokes from [$source], time: [${stopwatch.totalTimeSeconds} s]")
    }
}