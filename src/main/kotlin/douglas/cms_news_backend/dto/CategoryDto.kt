package douglas.cms_news_backend.dto

import org.bson.types.ObjectId

data class CategoryDto(
    val id : String,
    val name: String,
    val description: String,
    val authorName : String
)
