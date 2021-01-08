package hu.herolds.projects.morale.controller

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.service.JokeService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/morale-boost/")
class MoraleAssistantController(
    private val jokeService: JokeService
) {
    @GetMapping("/{id}")
    fun getJoke(@PathVariable(name = "id", required = true) id: Long): ResponseEntity<JokeDto> {
        log.info("Requested for joke with id: [$id]")
        return ResponseEntity.ok(jokeService.getJoke(id))
    }

    @GetMapping("/random/{lang}")
    fun getRandomJoke(@PathVariable(name = "lang", required = true) language: Language): ResponseEntity<JokeDto> {
        log.info("Requested for a random joke in [$language] language")
        return ResponseEntity.ok(jokeService.getRandomJoke(language))
    }

    companion object {
        private val log = LoggerFactory.getLogger(MoraleAssistantController::class.java)
    }
}