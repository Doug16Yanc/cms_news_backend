package douglas.cms_news_backend.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "categories")
data class Category(
    @Id
    val id: ObjectId? = null,

    var name: String,

    var description: String? = null,

    var slug: String? = null,

    @DBRef
    val author: User,

    val createdAt: LocalDateTime? = LocalDateTime.now(),

    var updatedAt: LocalDateTime? = LocalDateTime.now()
) {
    companion object {
        fun generateSlug(name: String): String {
            return name.lowercase()
                .replace(" ", "-")
                .replace("[^a-z0-9-]".toRegex(), "")
        }
    }
}