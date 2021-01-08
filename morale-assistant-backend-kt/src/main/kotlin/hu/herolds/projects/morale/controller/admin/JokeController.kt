package hu.herolds.projects.morale.controller.admin

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.controller.dto.paging.JokeSearchRequest
import hu.herolds.projects.morale.controller.dto.paging.PagedResponse
import hu.herolds.projects.morale.service.JokeService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/admin/joke/")
class JokeController(
    private val jokeService: JokeService
) {
    @PostMapping
    fun saveJoke(@RequestBody @Validated jokeDto: JokeDto) {
        log.info("Saving new joke: [$jokeDto]")
        jokeService.saveJoke(jokeDto)
    }

    @PutMapping("/{id}")
    fun updateJoke(@RequestBody @Validated jokeDto: JokeDto, @PathVariable("id") id: Long) {
        log.info("Updating joke with id: [${id}]")
        jokeService.updateJoke(id, jokeDto)
    }

    @DeleteMapping("/{id}")
    fun deleteJoke(@PathVariable("id") id: Long) {
        log.info("Deleting joke with id: [$id]")
        jokeService.deleteJoke(id)
    }

    @PostMapping("/search")
    fun searchJokes(
        @RequestBody request: JokeSearchRequest
    ): ResponseEntity<PagedResponse<JokeDto>> {
        log.info("Searching jokes: [$request]")
        return ok(jokeService.searchJokes(request))
    }

    companion object {
        private val log = LoggerFactory.getLogger(JokeController::class.java)
    }
}
