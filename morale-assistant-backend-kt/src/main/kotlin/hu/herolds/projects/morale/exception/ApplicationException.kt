package hu.herolds.projects.morale.exception

import java.lang.RuntimeException

sealed class ApplicationException(
    message: String? = null,
    cause: Throwable? = null
): RuntimeException(message, cause)

class SynthesizeException(
    message: String? = null,
    cause: Throwable? = null
): ApplicationException(message, cause)

class GetRandomJokeException(
    message: String?
): ApplicationException(message)

sealed class ResourceException(
    val id: Any,
    message: String?,
): ApplicationException(message)

class ResourceNotFoundException(
    id: Any,
    message: String?,
): ResourceException(id, message)
