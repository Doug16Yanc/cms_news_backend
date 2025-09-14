package douglas.cms_news_backend.dto.stocks

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class StockResult(
    val symbol: String,
    val historicalDataPrice: List<HistoricalPrice>
)