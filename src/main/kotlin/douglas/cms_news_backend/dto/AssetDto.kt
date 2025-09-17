package douglas.cms_news_backend.dto

import java.math.BigDecimal

data class AssetDto(
    val code : String,
    val lastPrice : BigDecimal,
    val dayHigh : BigDecimal,
    val dayLow : BigDecimal
)