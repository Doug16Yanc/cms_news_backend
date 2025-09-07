package douglas.cms_news_backend.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "tags")
data class Tag(
    @Id
    val id: ObjectId? = null,

    var name: String,

    var slug: String,

    val createdAt : LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun generateSlug(name: String): String {
            return name.lowercase()
                .replace(" ", "-")
                .replace("[^a-z0-9-]".toRegex(), "")
        }
    }
}
