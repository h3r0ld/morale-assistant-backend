package hu.herolds.projects.morale.controller.admin

import hu.herolds.projects.morale.config.JokeApiClientParameters
import hu.herolds.projects.morale.controller.dto.AvailableJokeSource
import hu.herolds.projects.morale.service.imports.client.JokeImportService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Min

@RestController
@RequestMapping("/api/admin/import", produces = [MediaType.APPLICATION_JSON_VALUE])
class JokeImportController(
    private val jokeImportService: JokeImportService,
    private val jokeApiClientParameters: JokeApiClientParameters,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/sources")
    fun getJokeSources() = jokeApiClientParameters.apiParams

    @GetMapping
    @ResponseStatus(CREATED)
    fun importJokes(source: AvailableJokeSource, @Min(1) count: Int = 1) {
        log.info("Importing jokes from [$source], count: [$count]")
        jokeImportService.importJokes(source = source, count = count)
    }
}