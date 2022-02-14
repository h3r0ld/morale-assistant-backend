package hu.herolds.projects.morale.service.sounds

import hu.herolds.projects.morale.domain.Joke
import java.net.URI

interface SoundStorage {
    fun load(joke: Joke): ByteArray
    fun save(fileContent: ByteArray): URI
}