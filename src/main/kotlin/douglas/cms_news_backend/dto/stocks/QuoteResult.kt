package douglas.cms_news_backend.dto.stocks

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuoteResult(
    val symbol: String,
    @param:JsonProperty("logourl")
    val logoUrl: String,
    val shortName: String,
    val longName: String?,
    val regularMarketPrice: Double?,
    val regularMarketChange: Double?,
    val regularMarketChangePercent: Double?,
    val historicalDataPrice: List<ApiHistoricalPrice>?
)