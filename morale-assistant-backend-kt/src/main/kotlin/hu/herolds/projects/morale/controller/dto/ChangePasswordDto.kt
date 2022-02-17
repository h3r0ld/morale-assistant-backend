package hu.herolds.projects.morale.controller.dto

data class ChangePasswordDto(
    val oldPassword: String,
    val newPassword: String,
    val newPasswordConfirm: String,
)