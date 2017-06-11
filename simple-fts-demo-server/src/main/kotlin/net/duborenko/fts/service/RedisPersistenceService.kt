package net.duborenko.fts.service

import net.duborenko.fts.model.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
@Profile("redis")
class RedisPersistenceService : PersistenceService {

    private @Autowired lateinit var redisTemplate: RedisTemplate<String, Document>

    override fun save(documents: Collection<Document>) {
        redisTemplate
                .delete("documents")
        redisTemplate.boundListOps("documents")
                .rightPushAll(*documents.toTypedArray())
    }

    override fun load(): List<Document>? = redisTemplate.boundListOps("documents").range(0, -1)
}