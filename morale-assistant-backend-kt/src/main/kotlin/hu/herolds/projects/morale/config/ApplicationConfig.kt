package hu.herolds.projects.morale.config

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.texttospeech.v1.TextToSpeechClient
import com.google.cloud.texttospeech.v1.TextToSpeechSettings
import marytts.LocalMaryInterface
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableRetry
class ApplicationConfig {

    @Bean
    fun localMaryInterface(): LocalMaryInterface = LocalMaryInterface().apply {
        log.info("Initialized Mary TTS!")
        log.info("Available locales: ${this.availableLocales}, available voices: ${this.availableVoices}")
    }

    @Bean
    fun textToSpeechClient(applicationParameters: ApplicationParameters): TextToSpeechClient {
        val credentials = GoogleCredentials.fromStream(applicationParameters.googleCredentialsFile.inputStream)
            .createScoped("https://www.googleapis.com/auth/cloud-platform")

        val textToSpeechSettings = TextToSpeechSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()

        return TextToSpeechClient.create(textToSpeechSettings)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ApplicationConfig::class.java)
    }
}
