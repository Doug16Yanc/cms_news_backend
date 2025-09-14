package douglas.cms_news_backend.dto.stocks

data class StockDetails(
    val summary: StockSummary,
    val history: List<HistoricalDataPoint>
)