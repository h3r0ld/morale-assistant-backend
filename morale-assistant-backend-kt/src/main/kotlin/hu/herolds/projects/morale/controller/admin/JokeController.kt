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

@RestController
@RequestMapping("/admin/joke")
class JokeController(
    private val jokeService: JokeService
) {
    @PostMapping
    fun saveJoke(@RequestBody @Validated jokeDto: JokeDto) {
        log.info("Saving new joke: [$jokeDto]")
        jokeService.saveJoke(jokeDto)
    }

    @GetMapping("/{id}")
    fun getJoke(@PathVariable("id") id: Long): JokeDto {
        log.info("Getting joke with id: [$id]")
        return jokeService.getJoke(id)
    }

    @GetMapping("/{id}/sound")
    fun getJokeSound(@PathVariable("id") id: Long): ByteArray? {
        return jokeService.getJoke(id).soundFile
    }

    @PutMapping("/{id}")
    fun updateJoke(@RequestBody @Validated jokeDto: JokeDto, @PathVariable("id") id: Long) {
        log.info("Updating joke with id: [$id]")
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
    ): PagedResponse<JokeDto> {
        log.info("Searching jokes: [$request]")
        return jokeService.searchJokes(request)
    }

    companion object {
        private val log = LoggerFactory.getLogger(JokeController::class.java)
    }
}
