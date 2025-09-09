package douglas.cms_news_backend.controller

import douglas.cms_news_backend.dto.LoginRequest
import douglas.cms_news_backend.dto.LoginResponse
import douglas.cms_news_backend.dto.UserDto
import douglas.cms_news_backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginRequest: LoginRequest) : ResponseEntity<LoginResponse> {
        var token = authService.login(loginRequest)

        return ResponseEntity.ok(token)
    }

    @GetMapping("/me")
    fun getMe(authentication: Authentication?): ResponseEntity<UserDto> {
        return ResponseEntity.ok(authentication?.let { authService.getInfo(it) })
    }
}