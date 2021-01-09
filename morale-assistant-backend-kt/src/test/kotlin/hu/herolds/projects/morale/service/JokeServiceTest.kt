package hu.herolds.projects.morale.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import hu.herolds.projects.morale.config.ApplicationParameters
import hu.herolds.projects.morale.controller.MoraleAssistantController
import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.repository.JokeRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.net.URI
import java.nio.file.Paths

@SpringBootTest
@ActiveProfiles("test")
class JokeServiceIT {
    @BeforeEach
    fun initialize() {
        File("temp").mkdir()
    }

    @AfterEach
    fun cleanup() {
        val tempDir = File("temp")
        if (tempDir.exists() && tempDir.isDirectory) {
            File("temp").deleteRecursively()
        }
    }
}

@SpringBootTest
@ActiveProfiles("test")
class JokeServiceRetryableIT(
    @Autowired private val moraleAssistantController: MoraleAssistantController,
    @Autowired private val applicationParameters: ApplicationParameters,
    @Autowired private val jokeRepository: JokeRepository,
): JokeServiceIT() {
    @SpyBean
    private lateinit var jokeService: JokeService
    @MockBean
    private lateinit var synthesizer: SynthesizerService

    @BeforeEach
    override fun cleanup() {
        super.cleanup()
        reset(jokeService)
        jokeRepository.deleteAll()
    }

    @TestFactory
    fun `test handleSoundFileNotFound recoveries`() = listOf<Pair<String,(controller: MoraleAssistantController, joke: Joke) -> ResponseEntity<JokeDto>>> (
        "getJoke" to { controller, joke -> controller.getJoke(joke.id!!) },
        "getRandomJoke" to {controller, _ ->  controller.getRandomJoke(Language.HU)}
    ).map { (displayName, call) ->
        DynamicTest.dynamicTest(displayName) {
            cleanup()
            initialize()

            File("temp/2.wav").writeText("content")
            val expectedPath = Paths.get("temp/2.wav")
            `when`(synthesizer.synthesize(any(), any())).thenReturn(expectedPath)

            val joke = jokeRepository.save(Joke(
                text = "Cica",
                language = Language.HU,
                soundFilePath = URI("non-existent-path/1.wav")
            ))

            val jokeDto = call(moraleAssistantController, joke).body!!

            verify(jokeService , times(1)).handleSoundFileNotFound(any())

            jokeRepository.findByIdOrNull(joke.id!!)!!.apply {
                assertEquals(expectedPath.toUri().path, soundFilePath!!.path)
                assertEquals(text, jokeDto.text)
                assertEquals(language, jokeDto.language)
            }
        }
    }

    @Test
    fun `getRandomJoke - retries`() {
        // Given
        initialize()

        File("temp/2.wav").writeText("content")
        val expectedPath = Paths.get("temp/2.wav")
        `when`(synthesizer.synthesize(any(), any())).thenReturn(expectedPath)

        // When, Then
        moraleAssistantController.getRandomJoke(language = Language.EN).body!!.apply {
            Assertions.assertNull(id)
            Assertions.assertNull(created)
            Assertions.assertNull(lastModified)
            assertEquals(JokeService.GENERAL_JOKE_TEXT, text)
            assertEquals(Language.EN, language)
        }

        verify(jokeService, times(applicationParameters.randomJoke.maxAttempts)).getRandomJoke(any())
    }
}