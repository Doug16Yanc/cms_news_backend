package douglas.cms_news_backend.initializer

import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.service.RoleService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class RoleInitializer(
    private val roleService: RoleService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val roles = listOf("JORNALISTA", "EDITOR", "VISITANTE")

        roles.forEach { roleName ->
            val role = try {
                roleService.getByName(roleName)
            } catch (e: EntityNotFoundException) {
                null
            }

            if (role == null) {
                roleService.create(roleName)
                println("Role criada: $roleName")
            } else {
                println("Role já existe: $roleName")
            }
        }
    }
}
