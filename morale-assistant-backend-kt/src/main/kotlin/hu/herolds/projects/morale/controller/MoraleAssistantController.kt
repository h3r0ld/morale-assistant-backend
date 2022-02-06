package hu.herolds.projects.morale.controller

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.service.JokeService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/public/morale-boost/", produces = [APPLICATION_JSON_VALUE])
class MoraleAssistantController(
    private val jokeService: JokeService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/{id}")
    fun getJoke(@PathVariable(name = "id", required = true) id: UUID): JokeDto {
        log.info("Requested for joke with id: [$id]")
        return jokeService.getJoke(id)
    }

    @GetMapping("/random/{lang}")
    fun getRandomJoke(@PathVariable(name = "lang", required = true) language: Language): JokeDto {
        log.info("Requested for a random joke in [$language] language")
        return jokeService.getRandomJoke(language)
    }
}
