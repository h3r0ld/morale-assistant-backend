package hu.herolds.projects.morale.controller.admin

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.service.JokeService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/joke/")
class JokeController(
    private val jokeService: JokeService
) {

    @PostMapping
    fun saveJoke(@RequestBody @Validated jokeDto: JokeDto) {
        jokeService.saveJoke(jokeDto)
    }

    @GetMapping
    fun getJokes(): ResponseEntity<List<JokeDto>> = ResponseEntity.ok(jokeService.getJokes())
}