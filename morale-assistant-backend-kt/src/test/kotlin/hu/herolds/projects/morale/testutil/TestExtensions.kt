package hu.herolds.projects.morale.testutil

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.Joke
import hu.herolds.projects.morale.util.isBetween
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.LocalDateTime

fun Joke?.assertEquals(expected: JokeDto) {
    assertNotNull(this)
    this?.also {
        assertEquals(expected.id, id)
        assertEquals(expected.text, text)
        assertEquals(expected.language, language)
        expected.created?.let {
            assertTrue(created.isVeryClose(to = it))
        }
        expected.lastModified?.let {
            assertTrue(lastModified.isVeryClose(to = it))
        }
        soundFilePath?.also {
            assertNotNull(expected.soundFile)
        }
    }
}

fun LocalDateTime.isVeryClose(to: LocalDateTime) = this.isBetween(to.minusMinutes(1), to.plusMinutes(1))
