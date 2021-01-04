package hu.herolds.projects.morale.controller

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.service.JokeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/morale-boost/")
class MoraleAssistantController(
    private val jokeService: JokeService
) {

    @GetMapping("/{lang}/")
    fun getNextJoke(@PathVariable(name = "lang", required = true) language: Language): ResponseEntity<JokeDto> {
        return ResponseEntity.ok(jokeService.getRandomJoke(language))
    }
}