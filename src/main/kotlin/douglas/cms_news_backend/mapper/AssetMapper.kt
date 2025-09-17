package douglas.cms_news_backend.mapper

import douglas.cms_news_backend.dto.AssetDto
import douglas.cms_news_backend.dto.AssetPageDto
import douglas.cms_news_backend.model.Asset
import org.springframework.data.domain.Page

object AssetMapper {
    fun convertToAssetPageDto(assetPage: Page<Asset?>): AssetPageDto {
        val assetDtos: List<AssetDto> = assetPage.content
            .filterNotNull()
            .map { asset -> convertToAssetDto(asset) }

        return AssetPageDto(
            content = assetDtos,
            currentPage = assetPage.number,
            totalPages = assetPage.totalPages,
            totalElements = assetPage.totalElements,
            pageSize = assetPage.size
        )
    }


    fun convertToAssetDto(asset: Asset): AssetDto {
        return AssetDto(
            asset.code,
            asset.lastPrice,
            asset.dayHigh,
            asset.dayLow,
        )
    }
}