package hu.herolds.projects.morale.controller.dto

data class ErrorResponse(
    val errors: List<ErrorDto>
)

data class ErrorDto(
        val title: String,
        val details: String,
)
