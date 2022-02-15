package hu.herolds.projects.morale.service.imports.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import hu.herolds.projects.morale.controller.dto.AvailableJokeSource
import hu.herolds.projects.morale.controller.dto.AvailableJokeSource.CHUCK_NORRIS_API
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChuckNorrisJoke(
    @get:JsonProperty("value")
    override val value: String,
): ImportableJoke

@FeignClient(name = "chuckNorrisApiClient", url = "\${client.api.CHUCK_NORRIS_API.base-url}")
interface ChuckNorrisApiFeignClient {

    // User-Agent is needed, otherwise its 403 (1010) Forbidden
    @GetMapping("/jokes/random", headers = ["User-Agent=Morale-Assistant"])
    fun getRandomJoke(): ChuckNorrisJoke
}

@Service
class ChuckNorrisApiService(
    private val apiClient: ChuckNorrisApiFeignClient,
): JokeApiService {
    override val source: AvailableJokeSource = CHUCK_NORRIS_API

    override fun getRandomJoke() = apiClient.getRandomJoke()
}