package douglas.cms_news_backend.dto.stocks

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BrapiResponse(
    val stocks: List<StockInfo>
)
