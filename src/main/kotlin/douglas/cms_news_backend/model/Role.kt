package douglas.cms_news_backend.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "roles")
data class Role(
    @Id
    val id: ObjectId? = null,
    val name: String
) {
    enum class Values(val value: String) {
        JORNALISTA("JORNALISTA"),
        EDITOR("EDITOR"),
        VISITANTE("VISITANTE")
    }

    companion object {
        fun fromEnum(roleValue: Values): Role {
            return Role(name = roleValue.value)
        }
    }
}