package hu.herolds.projects.morale.exception

import java.lang.RuntimeException

class UnauthorizedException(
        message: String
): RuntimeException(message)
