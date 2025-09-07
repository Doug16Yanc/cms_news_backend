package douglas.cms_news_backend.dto

import douglas.cms_news_backend.model.Role

data class ResponseUserDto(
    val name: String,
    val email: String,
    val role: Role
)