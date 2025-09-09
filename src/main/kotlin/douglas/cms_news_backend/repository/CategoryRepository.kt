package douglas.cms_news_backend.repository

import douglas.cms_news_backend.model.Category
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface CategoryRepository : MongoRepository<Category, ObjectId> {

    fun findCategoryById(categoryId: String): Category

    fun findBySlug(slug: String): Optional<Category>

    fun findByName(name: String): Category?

}