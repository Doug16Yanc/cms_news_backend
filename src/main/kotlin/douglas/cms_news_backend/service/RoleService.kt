package douglas.cms_news_backend.service

import douglas.cms_news_backend.exception.local.EntityAlreadyExistsException
import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.Role
import douglas.cms_news_backend.repository.RoleRepository
import org.springframework.stereotype.Service

@Service
class RoleService(private val roleRepository: RoleRepository) {

    fun create(name : String): Role {
        val alreadyRole = roleRepository.findByName(name)

        if (alreadyRole != null) {
            throw EntityAlreadyExistsException("Essa role já está cadastrada na base de dados.")
        }

        val newRole = roleRepository.save(Role(name = name))

        return roleRepository.save(newRole)
    }

    fun getByName(name: String): Role? {
        var role: Role? = roleRepository.findByName(name) ?: throw EntityNotFoundException("Role não encontrada na base de dados.")

        return role
    }
}