package net.duborenko.fts

import com.fasterxml.jackson.databind.ObjectMapper
import net.duborenko.fts.model.Document
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.context.config.annotation.EnableContextCredentials
import org.springframework.cloud.aws.context.config.annotation.EnableContextRegion
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import java.util.UUID

/**
 * @author Kiryl Dubarenka
 */
@Configuration
@Profile(AwsConfiguration.AWS_PROFILE)
@EnableContextCredentials(
        accessKey = "#{systemEnvironment.AWS_ACCESS_KEY}",
        secretKey = "#{systemEnvironment.AWS_SECRET_KEY}"
)
@EnableContextRegion(
        region = "\${cloud.aws.region.static}"
)
@EnableContextResourceLoader
open class AwsConfiguration {

    @Bean open fun documents(
            @Value("\${cloud.aws.s3.bucket}") bucket: String,
            resourceLoader: ResourceLoader,
            objectMapper: ObjectMapper): MutableMap<UUID, Document> {
        try {
            val documentsResource = getDocumentsResource(bucket, resourceLoader)
            val documentsMap = objectMapper.readValue<MutableMap<UUID, Document>>(
                    documentsResource.inputStream,
                    objectMapper.typeFactory
                            .constructMapType(MutableMap::class.java, UUID::class.java, Document::class.java))
            return if (documentsResource is WritableResource)
                ListenableMap(documentsMap, documentsResource, objectMapper) else documentsMap
        } catch(e: Exception) {
            logger.warn("Unable to load initial documents.", e)
            return mutableMapOf()
        }
    }

    private fun getDocumentsResource(bucket: String, resourceLoader: ResourceLoader) =
            resourceLoader.getResource("s3://${bucket}/documents.json")

    companion object {
        const val AWS_PROFILE = "aws"

        private val logger = LoggerFactory.getLogger(AwsConfiguration::class.java)
    }

    class ListenableMap<K, V>(
            val base: MutableMap<K, V>,
            val documentsResource: WritableResource,
            val objectMapper: ObjectMapper) : MutableMap<K, V> by base {

        override fun put(key: K, value: V): V? = executeAndSave { base.put(key, value) }

        override fun remove(key: K): V? = executeAndSave { base.remove(key) }

        private fun <R> executeAndSave(operation: () -> R): R =
                try {
                    operation()
                } finally {
                    saveIntoS3()
                }

        private fun saveIntoS3() {
            try {
                documentsResource.outputStream.use {
                    objectMapper.writeValue(it, base)
                }
            } catch(e: Exception) {
                logger.warn("Unable to save documents.", e)
            }
        }

    }
}