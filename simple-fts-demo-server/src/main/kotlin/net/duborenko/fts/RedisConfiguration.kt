package net.duborenko.fts

import com.fasterxml.jackson.databind.ObjectMapper
import net.duborenko.fts.model.Document
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.support.collections.DefaultRedisMap
import java.util.UUID

/**
 * @author Kiryl Dubarenka
 */
@Configuration
@Profile(RedisConfiguration.REDIS_PROFILE)
open class RedisConfiguration {

    @Bean open fun documents(redisTemplate: RedisTemplate<String, Document>) =
            DefaultRedisMap<UUID, Document>("documents", redisTemplate)

    @Bean open fun redisTemplate(
            redisConnectionFactory: RedisConnectionFactory,
            mapper: ObjectMapper) =
            RedisTemplate<String, Document>().apply {
                connectionFactory = redisConnectionFactory
                keySerializer = StringRedisSerializer()
                hashKeySerializer = Jackson2JsonRedisSerializer(UUID::class.java).apply {
                    setObjectMapper(mapper)
                }
                hashValueSerializer = Jackson2JsonRedisSerializer(Document::class.java).apply {
                    setObjectMapper(mapper)
                }
            }

    companion object {
        const val REDIS_PROFILE = "redis"
    }
}