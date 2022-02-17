package hu.herolds.projects.morale.exception

import hu.herolds.projects.morale.domain.Joke

open class ApplicationException(
    message: String? = null,
    cause: Throwable? = null
): RuntimeException(message, cause)

class SynthesizeException(
    message: String? = null,
    cause: Throwable? = null
): ApplicationException(message, cause)

sealed class ResourceException(
    val id: Any,
    message: String?,
): ApplicationException(message)

open class ResourceNotFoundException(
    id: Any,
    message: String?,
): ResourceException(id, message)

class GetRandomJokeException(
        message: String?
): ResourceNotFoundException(id = "RANDOM_JOKE", message = message)

class SoundFileNotFoundException(
    val joke: Joke,
    message: String?,
    cause: Throwable?,
): ApplicationException(message, cause)