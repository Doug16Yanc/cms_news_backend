package douglas.cms_news_backend.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import douglas.cms_news_backend.exception.local.FileBadRequestException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CloudinaryService (
    private val cloudinary: Cloudinary
) {
    data class ImageUploadResult(
        val url: String,
        val publicId: String,
    )

    fun uploadFile(file: MultipartFile): ImageUploadResult {
        try {
            val options = mapOf(
                "format" to "jpg",
                "background" to "white",
                "quality" to "auto"
            )

            val uploadResult = cloudinary.uploader().upload(file.bytes, options)
            val url = uploadResult["url"] as String
            val publicId = uploadResult["public_id"] as String

            return ImageUploadResult(url, publicId)
        } catch (e: Exception) {
            throw FileBadRequestException("An error occurred uploading the file: ${e.message}")
        }
    }

    fun deleteFile(publicId: String) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
        } catch (e: Exception) {
            throw FileBadRequestException("An error occurred deleting the file: ${e.message}")
        }
    }
}