package net.duborenko.fts

import com.fasterxml.jackson.databind.ObjectMapper
import net.duborenko.fts.model.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import java.util.UUID

/**
 * @author Kiryl Dubarenka
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
open class Application {

    @Bean open fun ftsIndex() =
            FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>(
                    rank = { it.matches.values.sumBy { it.size } }
            )

    @Bean
    @Profile("redis")
    open fun redisTemplate(
            redisConnectionFactory: RedisConnectionFactory,
            mapper: ObjectMapper): RedisTemplate<String, Document> =
            RedisTemplate<String, Document>().apply {
                connectionFactory = redisConnectionFactory
                valueSerializer = Jackson2JsonRedisSerializer(Document::class.java).apply {
                    setObjectMapper(mapper)
                }
            }

    companion object {
        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(arrayOf(Application::class.java, AwsConfiguration::class.java), args)
        }
    }

}
