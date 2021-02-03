package hu.herolds.projects.morale.controller.advice

import hu.herolds.projects.morale.controller.dto.ErrorDto
import hu.herolds.projects.morale.controller.dto.ErrorResponse
import hu.herolds.projects.morale.exception.ApplicationException
import hu.herolds.projects.morale.exception.ResourceNotFoundException
import hu.herolds.projects.morale.util.toErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandlingAdvice {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleValidationException(exception: MethodArgumentNotValidException): ErrorResponse {
        log.error(VALIDATION_ERROR_TITLE, exception)
        return exception.toErrorResponse()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleValidationException(exception: HttpMessageNotReadableException): ErrorResponse {
        log.error(VALIDATION_ERROR_TITLE, exception)
        return exception.toErrorResponse()
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(NOT_FOUND)
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ErrorResponse {
        log.error("Resource not found exception occured", exception)
        return ErrorResponse(listOf(ErrorDto(title = "Resource not found", details = exception.message!!)))
    }

    @ExceptionHandler(ApplicationException::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleApplicationException(exception: ApplicationException): ErrorResponse {
        log.error("Handled error occured", exception)
        return ErrorResponse(listOf(ErrorDto(title = "Error", details = exception.message!!)))
    }

    companion object {
        const val VALIDATION_ERROR_TITLE: String = "Validation error"
        private val log = LoggerFactory.getLogger(ErrorHandlingAdvice::class.java)
    }
}
