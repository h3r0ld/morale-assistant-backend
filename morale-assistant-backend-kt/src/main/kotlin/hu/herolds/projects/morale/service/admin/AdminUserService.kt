package hu.herolds.projects.morale.service.admin

import hu.herolds.projects.morale.controller.dto.ChangePasswordDto
import hu.herolds.projects.morale.exception.ApplicationException
import hu.herolds.projects.morale.exception.ResourceNotFoundException
import hu.herolds.projects.morale.exception.UnauthorizedException
import hu.herolds.projects.morale.repository.AdminUserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminUserService(
    private val adminUserRepository: AdminUserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun changePassword(user: UserDetails, changePasswordDto: ChangePasswordDto) {
        val adminUser = adminUserRepository.findByUsername(user.username)
            ?: throw ResourceNotFoundException(id = user.username, message = "Could not find admin user with username [${user.username}]")

        if (!bCryptPasswordEncoder.matches(changePasswordDto.oldPassword, adminUser.password)) {
            throw ApplicationException("[${user.username}] - current password and given old password does not match.")
        }

        if (changePasswordDto.newPassword != changePasswordDto.newPasswordConfirm) {
            throw ApplicationException("[${user.username}] - new password and new password confirm does not match.")
        }

        adminUserRepository.save(adminUser.apply {
            password = bCryptPasswordEncoder.encode(changePasswordDto.newPassword)
        })

        log.info("Updated password for admin user - [${adminUser.username}]")
    }
}