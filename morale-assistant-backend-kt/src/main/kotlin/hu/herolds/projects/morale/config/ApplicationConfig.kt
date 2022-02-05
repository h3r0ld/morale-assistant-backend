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

@Configuration
@EnableRetry
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
        if (!applicationParameters.synthesizer.google.credentialsFile!!.exists()) {
            log.error("Could not find Google credentials JSON file: ${applicationParameters.synthesizer.google.credentialsFile}")
            throw IllegalArgumentException("Google credentials file does not exist!")
        }

        val credentials = GoogleCredentials.fromStream(applicationParameters.synthesizer.google.credentialsFile.inputStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")

        val textToSpeechSettings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build()

        return TextToSpeechClient.create(textToSpeechSettings)
    }

    @Bean
    fun openApi2(buildProperties: BuildProperties): OpenAPI = OpenAPI()
            .components(Components())
            .info(Info()
                    .title("Morale Assistant API")
                    .description("A RESTful service for boosting your morale.")
                    .version(buildProperties.version)
                    .contact(Contact()
                            .name("Kristof Herold")
                            .url("https://github.com/h3r0ld")
                            .email("kristof.herold@gmail.com")
                    )
            )
}
