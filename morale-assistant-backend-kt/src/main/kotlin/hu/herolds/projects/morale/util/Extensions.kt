package hu.herolds.projects.morale.util

import hu.herolds.projects.morale.controller.advice.VALIDATION_ERROR_TITLE
import hu.herolds.projects.morale.controller.dto.ErrorDto
import hu.herolds.projects.morale.controller.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URI
import java.time.LocalDateTime
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

private val log = LoggerFactory.getLogger("Extensions")

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

fun CriteriaBuilder.likeIgnoreCase(path: Path<String>, value: String): Predicate = like(lower(path), "%${value.lowercase()}%")

fun CriteriaBuilder.and(predicates: List<Predicate>): Predicate = and(*predicates.toTypedArray())

fun LocalDateTime.isBetween(from: LocalDateTime, to: LocalDateTime) = isBefore(to) && isAfter(from)

fun MethodArgumentNotValidException.toErrorResponse(): ErrorResponse = ErrorResponse(errors = this.bindingResult.allErrors.map(ObjectError::toErrorDto).toList())

fun HttpMessageNotReadableException.toErrorResponse(): ErrorResponse = ErrorResponse(errors = listOf(ErrorDto(title = VALIDATION_ERROR_TITLE, details = this.mostSpecificCause.message ?: "Unknown error.")))

fun ObjectError.toErrorDto(): ErrorDto = ErrorDto(title = VALIDATION_ERROR_TITLE, details = this.defaultMessage ?: "Unknown validation error.")
