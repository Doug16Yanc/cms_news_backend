package douglas.cms_news_backend.utils

import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.User
import douglas.cms_news_backend.service.UserService
import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class AuthUtil(private val userService: UserService) {

    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("Usuário não autenticado")

        val jwt = authentication.principal as? Jwt
            ?: throw IllegalStateException("Principal não é JWT")

        val userId = jwt.getClaimAsString("userId")
            ?: throw IllegalArgumentException("userId não encontrado no token")

        return userService.findUserById(ObjectId(userId))
            ?: throw EntityNotFoundException("Usuário não encontrado")
    }
}
