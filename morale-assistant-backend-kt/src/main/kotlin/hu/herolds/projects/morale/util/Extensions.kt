package hu.herolds.projects.morale.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URI
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

val log: Logger = LoggerFactory.getLogger("Extensions")

fun URI?.toByteArray(): ByteArray? = this?.let {
    try {
        val file = File(it.path)

        if (!file.exists()) {
            throw FileNotFoundException(it.path)
        }

        file.readBytes()
    } catch (e: IOException) {
        log.error("Could not load file from URI", e)
        throw e
    }
}

fun CriteriaBuilder.likeIgnoreCase(path: Path<String>, value: String)
    = like(lower(path), "%${value.toLowerCase()}%")

fun CriteriaBuilder.and(predicates: List<Predicate>) = and(*predicates.toTypedArray())