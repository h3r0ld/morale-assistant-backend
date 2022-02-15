package hu.herolds.projects.morale.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import hu.herolds.projects.morale.IntegrationTest
import hu.herolds.projects.morale.config.ApplicationParameters
import hu.herolds.projects.morale.controller.MoraleAssistantController
import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.domain.enums.Language
import hu.herolds.projects.morale.domain.enums.Language.EN
import hu.herolds.projects.morale.exception.ResourceNotFoundException
import hu.herolds.projects.morale.repository.JokeRepository
import hu.herolds.projects.morale.testutil.isVeryClose
import hu.herolds.projects.morale.util.isBetween
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.data.repository.findByIdOrNull
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime.now
import java.util.*

@IntegrationTest
class JokeServiceIT(
    @Autowired private val jokeRepository: JokeRepository,
) {
    @Autowired
    private lateinit var jokeService: JokeService
    @MockBean
    protected lateinit var synthesizer: SynthesizerService

    @BeforeEach
    fun initialize() {
        File("temp").mkdir()
        setupNextSynthesize()
    }

    @AfterEach
    fun cleanup() {
        val tempDir = File("temp")
        if (tempDir.exists() && tempDir.isDirectory) {
            File("temp").deleteRecursively()
        }
        jokeRepository.deleteAll()
    }

    @Test
    fun `test get joke - exists`() {
        // Given
        val joke = jokeRepository.save(Joke(
            language = EN,
            text = "Joke",
            soundFilePath = Paths.get("temp/2.wav").toUri()
        ))

        // When, Then
        jokeService.getJoke(joke.id!!).apply {
            assertNotNull(id)
            assertNotNull(soundFile)
            assertEquals(joke.language, language)
            assertEquals(joke.text, text)
            assertNotNull(created)
            assertTrue(created!!.isVeryClose(to = joke.created))
            assertNotNull(lastModified)
            assertTrue(lastModified!!.isVeryClose(to = joke.lastModified))
        }
    }

    @TestFactory
    fun `test joke managing - non-existent joke`() = listOf<Pair<String, (id: UUID)->Unit>>(
        "getJoke" to { id -> jokeService.getJoke(id) },
        "deleteJoke" to { id -> jokeService.deleteJoke(id) },
        "updateJoke" to { id -> jokeService.updateJoke(id, JokeDto(text = "Don'tCare", language = EN)) }
    ).map { (displayName, call) ->
        DynamicTest.dynamicTest(displayName) {
            // Given
            val expectedId: UUID = UUID.randomUUID()
            // When, Then
            assertThrows<ResourceNotFoundException> {
                call(expectedId)
            }.apply {
                assertEquals(expectedId, id)
            }
        }
    }

    @Test
    fun `test joke update`() {
        // Given
        val joke = jokeRepository.save(Joke(
            language = EN,
            text = "Joke",
            soundFilePath = Paths.get("temp/2.wav").toUri()
        ))

        val updateJokeDto = JokeDto(text = "Joke2", language = EN)

        // When, Then
        jokeService.updateJoke(id = joke.id!!, jokeDto = updateJokeDto)

        // Then
        jokeRepository.findByIdOrNull(joke.id!!)!!.apply {
            assertEquals(updateJokeDto.text, text)
            assertEquals(updateJokeDto.language, language)
        }
    }

    @Test
    fun `test save joke`() {
        // Given
        val jokeDto = JokeDto(language = EN, text = "Joke")

        // When
        val jokeId = jokeService.saveJoke(jokeDto = jokeDto)

        // Then
        jokeRepository.findByIdOrNull(jokeId).apply {
            assertNotNull(this)
            this?.also {
                assertEquals(jokeDto.language, it.language)
                assertEquals(jokeDto.text, it.text)
                assertTrue(it.created.isBetween(now().minusMinutes(1), now().plusMinutes(1)))
                assertNotNull(it.lastModified.isBetween(it.created, now().plusMinutes(1)))
                assertNotNull(it.soundFilePath)
            }
        }
    }

    private fun setupNextSynthesize(): Path {
        File("temp/2.wav").writeText("content")
        val expectedPath = Paths.get("temp/2.wav")
        `when`(synthesizer.synthesize(any(), any())).thenReturn(ByteArray(10))
        return expectedPath
    }
}

@IntegrationTest
class JokeServiceRetryableIT(
    @Autowired private val moraleAssistantController: MoraleAssistantController,
    @Autowired private val applicationParameters: ApplicationParameters,
    @Autowired private val jokeRepository: JokeRepository,
): JokeServiceIT(jokeRepository) {
    @SpyBean
    private lateinit var jokeService: JokeService

    @AfterEach
    override fun cleanup() {
        super.cleanup()
        reset(jokeService)
    }

    @TestFactory
    fun `test handleSoundFileNotFound recoveries`() = listOf<Pair<String,(controller: MoraleAssistantController, joke: Joke) -> JokeDto>> (
        "getJoke" to { controller, joke -> controller.getJoke(joke.id!!) },
        "getRandomJoke" to {controller, _ ->  controller.getRandomJoke(Language.HU)}
    ).map { (displayName, call) ->
        DynamicTest.dynamicTest(displayName) {
            cleanup()
            initialize()

            val joke = jokeRepository.save(Joke(
                text = "Cica",
                language = Language.HU,
                soundFilePath = URI("non-existent-path/1.wav")
            ))

            val jokeDto = call(moraleAssistantController, joke)

            verify(jokeService, times(1)).handleSoundFileNotFound(any())

            jokeRepository.findByIdOrNull(joke.id!!)!!.apply {
                assertNotEquals(soundFilePath, joke.soundFilePath)
                assertEquals(text, jokeDto.text)
                assertEquals(language, jokeDto.language)
            }
        }
    }

    @Test
    fun `getRandomJoke - retries`() {
        moraleAssistantController.getRandomJoke(language = EN).apply {
            assertNull(id)
            assertNull(created)
            assertNull(lastModified)
            assertEquals(GENERAL_JOKE_TEXT, text)
            assertEquals(EN, language)
        }

        verify(jokeService, times(applicationParameters.randomJoke.maxAttempts)).getRandomJoke(any())
    }
}
