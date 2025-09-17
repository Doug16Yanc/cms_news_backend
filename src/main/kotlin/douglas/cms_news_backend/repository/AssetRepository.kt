package douglas.cms_news_backend.repository

import douglas.cms_news_backend.model.Asset
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface AssetRepository : MongoRepository<Asset, ObjectId> {
    fun findByCode(code: String?): Optional<Asset?>?
    fun findByCode(
        code: String?,
        pageable: Pageable?
    ): Page<Asset?>?
}