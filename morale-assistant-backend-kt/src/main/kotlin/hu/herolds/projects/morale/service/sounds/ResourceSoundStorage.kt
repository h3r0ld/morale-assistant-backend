package hu.herolds.projects.morale.service.sounds

import hu.herolds.projects.morale.config.SoundsParams
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.exception.SoundFileNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import org.springframework.stereotype.Service
import java.lang.Exception
import java.net.URI
import java.util.*

@Service
class ResourceSoundStorage(
    private val resourceLoader: ResourceLoader,
    private val soundsParams: SoundsParams,
): SoundStorage {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun load(joke: Joke): ByteArray {
        log.info("Loading [${joke.id}] joke's sound [${joke.soundFilePath}] from [${soundsParams.storageName}]")
        return try {
            resourceLoader.getResource(joke.soundFilePath.toString())
                .inputStream
                .readBytes()
        } catch (ex: Exception) {
            throw SoundFileNotFoundException(joke = joke, message = "The sound file could not be found!", cause = ex)
        }
    }

    override fun save(fileContent: ByteArray): URI {
        val uri = URI("${soundsParams.basePath}/${UUID.randomUUID()}.wav")

        log.info("Saving [$uri] file to [${soundsParams.storageName}]")

        val resource = resourceLoader.getResource(uri.toString())

        (resource as WritableResource).outputStream.use {
            it.write(fileContent)
        }

        return uri
    }
}