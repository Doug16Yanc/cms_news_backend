package douglas.cms_news_backend.repository

import douglas.cms_news_backend.model.User
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {

    fun findUserById(id: ObjectId) : User

    fun findByEmail(email: String): User?

    fun findByRoleName(roleName: String, pageable: Pageable): Page<User>

}