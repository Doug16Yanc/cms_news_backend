package douglas.cms_news_backend.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "assets")
data class Asset (
    @Id
    val id: Object? = null,

    val code: String,

    val lastPrice : BigDecimal,

    val dayLow : BigDecimal,

    val dayHigh : BigDecimal
)