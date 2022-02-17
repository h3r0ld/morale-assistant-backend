package hu.herolds.projects.morale.service.imports.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import hu.herolds.projects.morale.controller.dto.AvailableJokeSource
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@JsonIgnoreProperties(ignoreUnknown = true)
data class Sv443JokeApiJoke(
    @get:JsonProperty("joke")
    override val value: String,
): ImportableJoke

@FeignClient(name = "sv443JokeApiClient", url = "\${client.api.SV443_JOKE_API.base-url}")
interface JokeApiFeignClient {

    @GetMapping("/joke/Any")
    fun getRandomJoke(@RequestParam type: String = "single"): Sv443JokeApiJoke
}

@Service
class Sv443JokeApiService(
    private val apiClient: JokeApiFeignClient,
): JokeApiService {
    override val source: AvailableJokeSource = AvailableJokeSource.SV443_JOKE_API

    override fun getRandomJoke() = apiClient.getRandomJoke()
}