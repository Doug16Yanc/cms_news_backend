package douglas.cms_news_backend.repository

import douglas.cms_news_backend.model.Role
import douglas.cms_news_backend.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface RoleRepository : MongoRepository<Role, ObjectId> {
    fun findByName(name: String): Role?
}