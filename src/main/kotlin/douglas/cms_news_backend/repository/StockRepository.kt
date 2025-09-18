package douglas.cms_news_backend.repository

import douglas.cms_news_backend.dto.stocks.BrapiResponse
import douglas.cms_news_backend.dto.stocks.QuoteResponse
import douglas.cms_news_backend.dto.stocks.QuoteResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Repository
class StockRepository(
    @param:Value("\${brapi.api.url}") private val apiUrl: String,
    @param:Value("\${brapi.api.key}") private val apiToken: String,
    webClientBuilder: WebClient.Builder
) {
    private val limit = 7
    private val range = "1mo"
    private val interval = "1d"
    val browserUserAgent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36" +
            " (KHTML, like Gecko) Chrome/140.0.0.0 Mobile Safari/537.36"
    private val webClient = webClientBuilder.baseUrl(apiUrl).build()

    fun fetchStockCodes(sortBy: String, sortOrder: String): List<String> {
        val resourceUri = "list?type=stock&sortBy=$sortBy&sortOrder=$sortOrder&limit=$limit&token=$apiToken"
        val response = webClient.get()
            .uri(resourceUri)
            .header("User-Agent", browserUserAgent)
            .retrieve()
            .bodyToMono(BrapiResponse::class.java)
            .block()
        return response?.stocks?.map { it.stock } ?: emptyList()
    }

    fun findStocksDetails(codes: List<String>): List<QuoteResult> {
        val resultsWithPossibleNulls = Flux.fromIterable(codes)
            .flatMap { code ->
                val resourceUri = "$code?range=$range&interval=$interval&token=$apiToken"
                webClient.get()
                    .uri(resourceUri)
                    .header("User-Agent", browserUserAgent)
                    .retrieve()
                    .bodyToMono(QuoteResponse::class.java)
                    .mapNotNull { it.results.firstOrNull() }
            }
            .collectList()
            .block() ?: emptyList()

        return resultsWithPossibleNulls.filterNotNull()
    }
}