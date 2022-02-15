package hu.herolds.projects.morale.service.imports.client

import hu.herolds.projects.morale.controller.dto.AvailableJokeSource

interface JokeApiService {
    val source: AvailableJokeSource

    fun getRandomJoke(): ImportableJoke
}