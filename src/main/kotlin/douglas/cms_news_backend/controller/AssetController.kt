package douglas.cms_news_backend.controller

import douglas.cms_news_backend.dto.AssetPageDto
import douglas.cms_news_backend.service.AssetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/asset")
class AssetController(private val assetService: AssetService) {

    @PostMapping("/create-asset/{assetCode}")
    fun createAsset(@PathVariable assetCode: String): ResponseEntity<String> {
        val asset = assetService.createAsset(assetCode);
        return ResponseEntity.ok("Asset " + asset.code + " criado com sucesso!");
    }

    @GetMapping("/all-assets")
    fun getAllAssets(
        @RequestParam(defaultValue = "") order: String,
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<AssetPageDto> {
        val response = assetService.findAllAssets(query, page, size, order);
        return ResponseEntity.ok(response);
    }
}
