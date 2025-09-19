package douglas.cms_news_backend.repository

import douglas.cms_news_backend.dto.stocks.BrapiResponse
import douglas.cms_news_backend.dto.stocks.QuoteResponse
import douglas.cms_news_backend.dto.stocks.QuoteResult
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class StockRepository(
    @param:Value("\${brapi.api.url}") private val apiUrl: String,
    @param:Value("\${brapi.api.key}") private val apiToken: String,
    webClientBuilder: WebClient.Builder
) {
    private val logger = LoggerFactory.getLogger(StockRepository::class.java)
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
                val resourceUri = "quote/$code?range=$range&interval=$interval&token=$apiToken"
                webClient.get()
                    .uri(resourceUri)
                    .header("User-Agent", browserUserAgent)
                    .retrieve()
                    .onStatus({ status -> status == HttpStatus.NOT_FOUND }) { response ->
                        logger.warn("Ticker $code não encontrado na API Brapi")
                        Mono.empty()
                    }
                    .onStatus({ status -> status.is4xxClientError }) { response ->
                        logger.warn("Erro cliente para ticker $code: ${response.statusCode()}")
                        Mono.empty()
                    }
                    .onStatus({ status -> status.is5xxServerError }) { response ->
                        logger.error("Erro servidor para ticker $code: ${response.statusCode()}")
                        Mono.error(WebClientResponseException(
                            response.statusCode().value(),
                            "Erro na API Brapi para ticker $code",
                            response.headers().asHttpHeaders(),
                            null,
                            null
                        ))
                    }
                    .bodyToMono(QuoteResponse::class.java)
                    .mapNotNull { it.results.firstOrNull() }
                    .onErrorResume(WebClientResponseException.NotFound::class.java) { ex ->
                        logger.warn("Ticker $code não encontrado (404)")
                        Mono.empty()
                    }
                    .onErrorResume(WebClientResponseException::class.java) { ex ->
                        logger.error("Erro na chamada para ticker $code: ${ex.statusCode} - ${ex.message}")
                        Mono.empty()
                    }
                    .onErrorResume { ex ->
                        logger.error("Erro inesperado para ticker $code: ${ex.message}")
                        Mono.empty()
                    }
            }
            .collectList()
            .block() ?: emptyList()

        return resultsWithPossibleNulls.filterNotNull()
    }
}