package net.duborenko.fts.service

import com.fasterxml.jackson.databind.ObjectMapper
import net.duborenko.fts.AwsConfiguration
import net.duborenko.fts.model.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import org.springframework.stereotype.Service

/**
 * @author Kiryl Dubarenka
 */
@Service
@Profile(AwsConfiguration.AWS_PROFILE)
class S3PersistenceService : PersistenceService {

    @Value("\${cloud.aws.s3.bucket}") lateinit var bucket: String

    @Autowired lateinit var resourceLoader: ResourceLoader
    @Autowired lateinit var objectMapper: ObjectMapper

    override fun save(documents: Collection<Document>) {
        val documentsResource = getDocumentsResource()
        if (documentsResource is WritableResource) {
            documentsResource.outputStream.use {
                objectMapper.writeValue(it, documents)
            }
        }
    }

    override fun load(): List<Document>? =
            objectMapper.readValue(getDocumentsResource().inputStream,
                    objectMapper.typeFactory
                            .constructCollectionType(List::class.java, Document::class.java))

    private fun getDocumentsResource() = resourceLoader.getResource("s3://${bucket}/documents.json")

}
