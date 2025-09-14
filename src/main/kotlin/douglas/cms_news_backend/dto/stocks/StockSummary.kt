package douglas.cms_news_backend.dto.stocks

data class StockSummary(
    val symbol: String,
    val logoUrl: String,
    val shortName: String,
    val longName: String?,
    val value: String,
    val change: ChangeInfo,
    val positive: Boolean
)