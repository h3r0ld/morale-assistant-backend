package hu.herolds.projects.morale.config

import com.google.api.client.util.Base64
import hu.herolds.projects.morale.controller.dto.AvailableJokeSource
import hu.herolds.projects.morale.controller.dto.JokeSource
import hu.herolds.projects.morale.domain.enums.Language
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.io.Resource
import java.net.URI
import java.net.URL
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

@ConstructorBinding
@ConfigurationProperties(prefix = "client")
data class JokeApiClientParameters(
    val api: Map<AvailableJokeSource, JokeApiClientParams>
) {
    val apiParams = api.map { (source, params) -> JokeSource(name = source, url = params.baseUrl, language = params.language) }
}

data class JokeApiClientParams(
    val baseUrl: URL,
    val language: Language
)

data class RandomJokeParams(val maxAttempts: Int)

data class SynthesizerParams(val google: GoogleSynthesizer)

data class GoogleSynthesizer(
        val enabled: Boolean,
        var credentialsFile: Resource? = null,
        val credentialsBase64: String? = null
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
