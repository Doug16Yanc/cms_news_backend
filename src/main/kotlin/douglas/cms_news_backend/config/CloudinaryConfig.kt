package douglas.cms_news_backend.config

import com.cloudinary.Cloudinary
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CloudinaryConfig (
    @param:Value("\${cloudinary.cloud_name}") val cloudName: String,
    @param:Value("\${cloudinary.api_key}") val apiKey: String,
    @param:Value("\${cloudinary.api_secret}") val apiSecret: String
) {
    @Bean
    fun cloudinary(): Cloudinary {
        val config = mapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret,
        )

        return Cloudinary(config)
    }
}