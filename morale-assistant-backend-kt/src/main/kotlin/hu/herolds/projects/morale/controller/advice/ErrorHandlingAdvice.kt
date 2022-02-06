package hu.herolds.projects.morale.controller.advice

import hu.herolds.projects.morale.controller.dto.ErrorDto
import hu.herolds.projects.morale.controller.dto.ErrorResponse
import hu.herolds.projects.morale.exception.ApplicationException
import hu.herolds.projects.morale.exception.ResourceNotFoundException
import hu.herolds.projects.morale.util.toErrorResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

const val VALIDATION_ERROR_TITLE: String = "Validation error"

@RestControllerAdvice
class ErrorHandlingAdvice {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = [
            Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun handleValidationException(exception: MethodArgumentNotValidException): ErrorResponse {
        log.error(VALIDATION_ERROR_TITLE, exception)
        return exception.toErrorResponse()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = [
            Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun handleValidationException(exception: HttpMessageNotReadableException): ErrorResponse {
        log.error(VALIDATION_ERROR_TITLE, exception)
        return exception.toErrorResponse()
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = [
            Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun handleValidationException(exception: MethodArgumentTypeMismatchException): ErrorResponse {
        log.error(VALIDATION_ERROR_TITLE, exception)
        return ErrorResponse(listOf(ErrorDto(title = VALIDATION_ERROR_TITLE, details = "Looks like something was not right with your request!")))
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(NOT_FOUND)
    @ApiResponse(
        responseCode = "404",
        description = "Resource not found",
        content = [
            Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ErrorResponse {
        log.error("Resource not found exception occured", exception)
        return ErrorResponse(listOf(ErrorDto(title = "Resource not found", details = exception.message!!)))
    }

    @ExceptionHandler(ApplicationException::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiResponse(
        responseCode = "500",
        description = "Application error",
        content = [
            Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun handleApplicationException(exception: ApplicationException): ErrorResponse {
        log.error("Handled error occured", exception)
        return ErrorResponse(listOf(ErrorDto(title = "Error", details = exception.message!!)))
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [
            Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))
        ]
    )
    fun handleUnexpectedException(exception: Exception): ErrorResponse {
        log.error("Unexpected exception", exception)
        return ErrorResponse(listOf(ErrorDto(title = "Error", details = "Unexpected error. :(")))
    }
}
