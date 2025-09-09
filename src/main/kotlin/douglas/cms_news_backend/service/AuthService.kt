package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.LoginRequest
import douglas.cms_news_backend.dto.LoginResponse
import douglas.cms_news_backend.dto.UserDto
import douglas.cms_news_backend.exception.local.BadCredentialsException
import douglas.cms_news_backend.exception.local.EntityNotFoundException
import douglas.cms_news_backend.model.User
import org.bson.types.ObjectId
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthService(
    private val userService: UserService,
    private val jwtEncoder: JwtEncoder,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    fun login(loginRequest: LoginRequest): LoginResponse {
        val user = userService.findUserEntityByEmail(loginRequest.email)
            ?: throw BadCredentialsException("Usuário não encontrado")

        if (!passwordEncoder.matches(loginRequest.password, user.password)) {
            throw BadCredentialsException("Senha incorreta")
        }

        val jwt = generateToken(user)

        return LoginResponse(
            token = jwt,
        )
    }

    private fun generateToken(user: User): String {
        val claims = JwtClaimsSet.builder()
            .subject(user.email)
            .claim("userId", user.getIdAsString())
            .claim("roles", listOf(user.role.name))
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    fun getInfo(authentication: Authentication): UserDto {
        val jwt = authentication.principal as Jwt

        val userIdString = jwt.getClaimAsString("userId")
            ?: throw IllegalArgumentException("UserId claim not found in token")

        val userId = ObjectId(userIdString)

        val currentUser = userService.findUserById(userId)
            ?: throw EntityNotFoundException("User not found!")

        return UserDto(
            name = currentUser.name,
            email = currentUser.email,
            role = currentUser.role
        )
    }
}