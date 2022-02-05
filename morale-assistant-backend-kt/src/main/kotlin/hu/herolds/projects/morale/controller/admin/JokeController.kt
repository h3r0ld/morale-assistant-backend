package hu.herolds.projects.morale.controller.admin

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.controller.dto.paging.JokeSearchRequest
import hu.herolds.projects.morale.service.JokeService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/admin/joke")
class JokeController(
    private val jokeService: JokeService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun saveJoke(@RequestBody @Validated jokeDto: JokeDto) {
        log.info("Saving new joke: [$jokeDto]")
        jokeService.saveJoke(jokeDto)
    }

    @GetMapping("/{id}")
    fun getJoke(@PathVariable("id") id: UUID): JokeDto {
        log.info("Getting joke with id: [$id]")
        return jokeService.getJoke(id)
    }

    @GetMapping("/{id}/sound")
    fun getJokeSound(@PathVariable("id") id: UUID): ByteArray? {
        return jokeService.getJoke(id).soundFile
    }

    @PutMapping("/{id}")
    fun updateJoke(@RequestBody @Validated jokeDto: JokeDto, @PathVariable("id") id: UUID) {
        log.info("Updating joke with id: [$id]")
        jokeService.updateJoke(id, jokeDto)
    }

    @DeleteMapping("/{id}")
    fun deleteJoke(@PathVariable("id") id: UUID) {
        log.info("Deleting joke with id: [$id]")
        jokeService.deleteJoke(id)
    }

    @PostMapping("/search")
    fun searchJokes(@RequestBody request: JokeSearchRequest): Page<JokeDto> {
        log.info("Searching jokes: [$request]")
        return jokeService.searchJokes(request)
    }
}
