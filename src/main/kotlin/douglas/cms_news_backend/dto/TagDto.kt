package douglas.cms_news_backend.dto

import org.bson.types.ObjectId

data class TagDto(
    val id: String,
    val name: String,
    val slug: String,
    val authorName : String
)