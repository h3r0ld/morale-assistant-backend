package hu.herolds.projects.morale.config

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.texttospeech.v1.TextToSpeechClient
import com.google.cloud.texttospeech.v1.TextToSpeechSettings
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import marytts.LocalMaryInterface
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class ApplicationConfig {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun localMaryInterface(): LocalMaryInterface = LocalMaryInterface().apply {
        log.info("Initialized Mary TTS!")
        log.info("Mary TTS - Available locales: ${this.availableLocales}, available voices: ${this.availableVoices}")
    }

    @Bean
    @ConditionalOnProperty(
            value = ["synthesizer.google.enabled"],
            havingValue = "true"
    )
    fun textToSpeechClient(applicationParameters: ApplicationParameters): TextToSpeechClient {
        if (applicationParameters.synthesizer.google.credentialsInputStream == null) {
            log.error("Could not find Google credentials JSON file: [${applicationParameters.synthesizer.google.credentialsFile}], credentials not given in base64 either.")
            throw IllegalArgumentException("Google credentials not found!")
        }

        val credentials = GoogleCredentials.fromStream(applicationParameters.synthesizer.google.credentialsInputStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")

        val textToSpeechSettings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build()

        return TextToSpeechClient.create(textToSpeechSettings)
    }

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()
}
