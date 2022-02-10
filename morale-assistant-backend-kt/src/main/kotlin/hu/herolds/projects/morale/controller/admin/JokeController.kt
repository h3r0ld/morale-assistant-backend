package hu.herolds.projects.morale.controller.admin

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.controller.dto.paging.JokeSearchRequest
import hu.herolds.projects.morale.service.JokeService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/admin/joke", produces = [APPLICATION_JSON_VALUE])
class JokeController(
    private val jokeService: JokeService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @ResponseStatus(CREATED)
    fun saveJoke(@RequestBody @Validated jokeDto: JokeDto): UUID {
        log.info("Saving new joke: [$jokeDto]")
        return jokeService.saveJoke(jokeDto)
    }

    @GetMapping("/{id}")
    fun getJoke(@PathVariable("id") id: UUID): JokeDto {
        log.info("Getting joke with id: [$id]")
        return jokeService.getJoke(id)
    }

    @PutMapping("/{id}")
    fun updateJoke(@RequestBody @Validated jokeDto: JokeDto, @PathVariable("id") id: UUID) {
        log.info("Updating joke with id: [$id]")
        jokeService.updateJoke(id, jokeDto)
    }

    /**
     * DeleteMapping is not supported by openapi-generator-cli (when generating typescript-angular for frontend)
     */
    @PostMapping("/delete")
    fun deleteJoke(@RequestBody ids: Set<UUID>) {
        log.info("Deleting jokes with ids: $ids")
        ids.forEach { id ->  jokeService.deleteJoke(id) }
    }

    @PostMapping("/search")
    fun searchJokes(@RequestBody request: JokeSearchRequest): Page<JokeDto> {
        log.info("Searching jokes: [$request]")
        return jokeService.searchJokes(request)
    }
}
