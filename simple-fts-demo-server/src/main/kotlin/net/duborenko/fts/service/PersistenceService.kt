package net.duborenko.fts.service

import net.duborenko.fts.model.Document

interface PersistenceService {

    fun save(documents: Collection<Document>)

    fun load(): List<Document>?

}
