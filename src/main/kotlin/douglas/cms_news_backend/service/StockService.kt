package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.*
import douglas.cms_news_backend.dto.stocks.ChangeInfo
import douglas.cms_news_backend.dto.stocks.HistoricalDataPoint
import douglas.cms_news_backend.dto.stocks.QuoteResult
import douglas.cms_news_backend.dto.stocks.StockDetails
import douglas.cms_news_backend.dto.stocks.StockSummary
import douglas.cms_news_backend.repository.StockRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class StockService(
    private val stockRepository: StockRepository
) {

    fun getExpensiveStocksSummary(): List<StockDetails> {
        val codes = stockRepository.fetchStockCodes(sortBy = "close", sortOrder = "desc")
        val details = stockRepository.findStocksDetails(codes)
        return details.map { it.toStockDetails() }
    }

    fun getCheapestStocksSummary(): List<StockDetails> {
        val codes = stockRepository.fetchStockCodes(sortBy = "close", sortOrder = "asc")
        val details = stockRepository.findStocksDetails(codes)
        return details.map { it.toStockDetails() }
    }

    fun getFeaturedStocksSummary(): List<StockDetails> {
        val codes = stockRepository.fetchStockCodes(sortBy = "market_cap_basic", sortOrder = "desc")
        val details = stockRepository.findStocksDetails(codes)
        return details.map { it.toStockDetails() }
    }

    private fun QuoteResult.toStockDetails(): StockDetails {
        val isPositive = (this.regularMarketChange ?: 0.0) >= 0

        val summary = StockSummary(
            symbol = this.symbol,
            value = String.format("%.2f", this.regularMarketPrice ?: 0.0).replace('.', ','),
            logoUrl = this.logoUrl,
            shortName = this.shortName,
            longName = this.longName,
            change = ChangeInfo(
                absolute = String.format("%+.2f", this.regularMarketChange ?: 0.0).replace('.', ','),
                percent = String.format("%+.2f%%", this.regularMarketChangePercent ?: 0.0).replace('.', ',')
            ),
            positive = isPositive
        )

        val history = this.historicalDataPrice?.map { pricePoint ->
            val dateFormatted = Instant.ofEpochSecond(pricePoint.date)
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            HistoricalDataPoint(
                date = dateFormatted,
                close = pricePoint.close
            )
        } ?: emptyList()

        return StockDetails(
            summary = summary,
            history = history
        )
    }
}
