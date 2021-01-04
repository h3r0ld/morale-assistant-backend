package hu.herolds.projects.morale.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@ConfigurationProperties
class ApplicationParameters(
    val sounds: SoundsParams,
    val googleCredentialsFile: Resource
) {
    val baseDirectory: Path = Paths.get(sounds.basePath)

    fun getNextFilePath(): Path = baseDirectory.resolve(UUID.randomUUID().toString() + ".wav")
}

class SoundsParams(val basePath: URI)