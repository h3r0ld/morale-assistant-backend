package hu.herolds.projects.morale.service.synthetize

import hu.herolds.projects.morale.domain.enums.Language
import java.nio.file.Path

interface Synthesizer {
    val supportedLanguages: Set<Language>

    fun synthesize(text: String): ByteArray
}