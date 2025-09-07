package douglas.cms_news_backend.repository

import douglas.cms_news_backend.model.Tag
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagRepository : MongoRepository<Tag, ObjectId> {

    fun findBySlug(slug: String): Optional<Tag>

    fun findByName(name: String): Optional<Tag>

    fun findAllByName(names: List<String>): List<Tag>
}