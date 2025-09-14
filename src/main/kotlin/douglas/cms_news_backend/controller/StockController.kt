package douglas.cms_news_backend.controller

import douglas.cms_news_backend.service.StockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("stocks")
class StockController (
    private val stockService: StockService
) {
    @GetMapping("/expensive")
    fun getExpensiveStocks() = ResponseEntity.ok(stockService.getExpensiveStocksSummary())

    @GetMapping("/cheapest")
    fun getCheapestStocks() = ResponseEntity.ok(stockService.getCheapestStocksSummary())

    @GetMapping("/featured")
    fun getFeaturedStocks() = ResponseEntity.ok(stockService.getFeaturedStocksSummary())
}