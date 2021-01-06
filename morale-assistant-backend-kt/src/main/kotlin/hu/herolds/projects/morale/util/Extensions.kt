package hu.herolds.projects.morale.util

import hu.herolds.projects.morale.service.JokeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URI

val log: Logger = LoggerFactory.getLogger("Extensions")

fun URI?.toByteArray(): ByteArray? = this?.let {
    try {
        File(it.path).readBytes()
    } catch (e: IOException) {
        log.error("Could not load file from URI", e)
        null
    }
}