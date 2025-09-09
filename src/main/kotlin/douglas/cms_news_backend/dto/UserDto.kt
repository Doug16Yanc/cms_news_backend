package douglas.cms_news_backend.dto

import douglas.cms_news_backend.model.Role

data class UserDto(
    val name : String,
    val email : String,
    val role: String
)