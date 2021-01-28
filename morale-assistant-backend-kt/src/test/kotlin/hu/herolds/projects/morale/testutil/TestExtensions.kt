package hu.herolds.projects.morale.testutil

import hu.herolds.projects.morale.controller.dto.JokeDto
import hu.herolds.projects.morale.domain.Joke
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

fun Joke?.assertEquals(expected: JokeDto) {
    assertNotNull(this)
    this?.also {
        assertEquals(expected.id, id)
        assertEquals(expected.text, text)
        assertEquals(expected.language, language)
        assertEquals(expected.created, created)
        assertEquals(expected.lastModified, lastModified)
        soundFilePath?.also {
            assertNotNull(expected.soundFile)
        }
    }
}