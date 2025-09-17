package douglas.cms_news_backend.service

import douglas.cms_news_backend.dto.AssetPageDto
import douglas.cms_news_backend.exception.local.ApiBrapiProblemException
import douglas.cms_news_backend.exception.local.EntityAlreadyExistsException
import douglas.cms_news_backend.mapper.AssetMapper
import douglas.cms_news_backend.model.Asset
import douglas.cms_news_backend.repository.AssetRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class AssetService(
    private val assetRepository: AssetRepository,
    private val restTemplate: RestTemplate
) {

    @Value("\${brapi.api.url}")
    private lateinit var baseUrl: String

    @Value("\${brapi.api.key}")
    private lateinit var token: String

    fun createAsset(assetCode: String): Asset {
        assetRepository.findByCode(assetCode)?.let {
            throw EntityAlreadyExistsException("Este ativo já está cadastrado.")
        }

        val url = UriComponentsBuilder.fromUri(URI.create(baseUrl))
            .path("/$assetCode")
            .queryParam("token", token)
            .toUriString()

        try {
            val response: ResponseEntity<MutableMap<*, *>> = restTemplate.exchange(
                url, HttpMethod.GET, null,
                MutableMap::class.java
            )

            if (response.statusCode == HttpStatus.OK) {
                val body = response.body
                    ?: throw ApiBrapiProblemException("Resposta vazia da BRAPI para: $assetCode")

                if (body.containsKey("results")) {
                    val assetData = (body["results"] as? List<*>)?.firstOrNull() as? Map<String, Any>
                        ?: throw ApiBrapiProblemException("Nenhum dado de ativo encontrado para: $assetCode")

                    val asset = Asset(
                        code = assetData["symbol"].toString(),
                        lastPrice = assetData["regularMarketPrice"].toString().toBigDecimal(),
                        dayHigh = assetData["regularMarketDayHigh"].toString().toBigDecimal(),
                        dayLow = assetData["regularMarketDayLow"].toString().toBigDecimal(),
                    )

                    val saved = assetRepository.save(asset)

                    return saved
                } else {
                    throw ApiBrapiProblemException("Nenhum dado de ativo encontrado para: $assetCode")
                }
            } else {
                throw ApiBrapiProblemException("Erro ao consultar api da BRAPI para: $assetCode")
            }
        } catch (ex: Exception) {
            throw ApiBrapiProblemException("Erro durante comunicação com a BRAPI: ${ex.message}")
        }
    }

    fun findAllAssets(query: String?, page: Int?, size: Int?, order: String?): AssetPageDto? {
        val pageable = when {
            order.equals("desc", ignoreCase = true) ->
                PageRequest.of(page ?: 0, size ?: 10, Sort.by(Sort.Direction.DESC, "dayLow"))

            order.equals("asc", ignoreCase = true) ->
                PageRequest.of(page ?: 0, size ?: 10, Sort.by(Sort.Direction.ASC, "dayLow"))

            else ->
                PageRequest.of(page ?: 0, size ?: 10)
        }

        val assets: Page<Asset?>? = assetRepository.findByCode(query, pageable)
        return assets?.let { AssetMapper.convertToAssetPageDto(it) }
    }
}
