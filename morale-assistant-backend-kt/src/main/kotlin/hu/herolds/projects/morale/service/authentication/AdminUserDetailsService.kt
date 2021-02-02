package hu.herolds.projects.morale.service.authentication

import hu.herolds.projects.morale.controller.dto.AdminUserDto
import hu.herolds.projects.morale.exception.UnauthorizedException
import hu.herolds.projects.morale.repository.AdminUserRepository
import hu.herolds.projects.morale.repository.findByUsernameOrNull
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AdminUserDetailsService(
        private val adminUserRepository: AdminUserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String?): AdminUserDto =
            (adminUserRepository.findByUsernameOrNull(username)
                    ?: throw UnauthorizedException("User not found: [$username]")).let { user ->
                AdminUserDto(username = user.username, password = user.password)
            }
}
