package hu.herolds.projects.morale

import com.google.cloud.texttospeech.v1.TextToSpeechClient
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.mock
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.nio.file.Files
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(RUNTIME)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TestConfig::class])
@ContextConfiguration(initializers = [TestInitializer::class])
@ActiveProfiles("test")
annotation class IntegrationTest

internal class TestInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val tempDir = Files.createTempDirectory("test").toUri()

        TestPropertyValues.of(
                "sounds.base-path=${tempDir}"
        ).applyTo(applicationContext.environment).let {
            log.info("Using sounds.base-path=${tempDir}")
        }
    }

    companion object {
private val log = LoggerFactory.getLogger(javaClass)
    }
}

@TestConfiguration
internal class TestConfig {
    @Bean
    fun textToSpeechClient(): TextToSpeechClient = mock(TextToSpeechClient::class.java)
}
