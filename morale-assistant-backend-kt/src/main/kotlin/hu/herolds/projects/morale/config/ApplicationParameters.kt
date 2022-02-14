package hu.herolds.projects.morale.config

import com.google.api.client.util.Base64
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.io.Resource
import java.net.URI
import kotlin.io.path.createTempFile

@ConstructorBinding
@ConfigurationProperties
data class ApplicationParameters(
    val sounds: SoundsParams,
    val synthesizer: SynthesizerParams,
    val randomJoke: RandomJokeParams,
)

@ConstructorBinding
@ConfigurationProperties(prefix = "sounds")
data class SoundsParams(
    val basePath: URI,
    val storageName: String
)

data class RandomJokeParams(val maxAttempts: Int)

data class SynthesizerParams(val google: GoogleSynthesizer)

data class GoogleSynthesizer(
        val enabled: Boolean,
        var credentialsFile: Resource?,
        val credentialsBase64: String?
) {

    val credentialsInputStream = credentialsFile?.inputStream
        ?: credentialsBase64?.let {
            val decodedCredentials = Base64.decodeBase64(it)

            createTempFile("google-credentials").toFile()
                .apply {
                    writeBytes(decodedCredentials)
                }.inputStream()
        }
}
