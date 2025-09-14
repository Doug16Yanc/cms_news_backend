package douglas.cms_news_backend.dto.stocks

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuoteResponse(val results: List<QuoteResult>)