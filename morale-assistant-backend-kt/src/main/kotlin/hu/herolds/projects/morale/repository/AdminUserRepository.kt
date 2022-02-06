package hu.herolds.projects.morale.repository

import hu.herolds.projects.morale.domain.AdminUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface AdminUserRepository: JpaRepository<AdminUser, UUID> {
    fun findByUsername(username: String?): AdminUser?
}
