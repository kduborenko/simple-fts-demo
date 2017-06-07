package net.duborenko.fts.controller

import net.duborenko.fts.FullTextSearchIndex
import net.duborenko.fts.SearchResult
import net.duborenko.fts.model.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.HtmlUtils
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * @author Kiryl Dubarenka
 */
@RestController
@RequestMapping("/fts")
class FullTextSearchApiController {

    private @Autowired lateinit var ftsIndex: FullTextSearchIndex<UUID, Document>

    private var documents = mutableMapOf<UUID, Document>()

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun add(@RequestBody unsafedocument: Document, uriBuilder: UriComponentsBuilder): ResponseEntity<Void> {
        val document = unsafedocument.copy(
                text = HtmlUtils.htmlEscape(unsafedocument.text))
        ftsIndex.add(document)
        documents[document.id] = document

        return ResponseEntity(
                HttpHeaders().apply {
                    location = uriBuilder
                            .path("/fts/{id}")
                            .buildAndExpand(document.id)
                            .toUri()
                },
                HttpStatus.CREATED)
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun search(@RequestParam("search", required = false) search: String) =
            if (search.isBlank()) documents.values.map { SearchResult(it, mapOf()) }
            else ftsIndex.search(search)

    @RequestMapping(method = arrayOf(RequestMethod.DELETE), value = "/{id}")
    fun search(@PathVariable("id") id: UUID) {
        ftsIndex.remove(id)
        documents.remove(id)
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/data-structure")
    fun dataStructure() = mapOf(
            *arrayOf("keywords", "index")
                    .map { it to getPropertyValue(ftsIndex, it) }
                    .toTypedArray()
    )

    private fun getPropertyValue(ftsIndex: FullTextSearchIndex<UUID, Document>, name: String): Any? {
        val property = ftsIndex::class.memberProperties.first { it.name == name }
        property.isAccessible = true
        return property.call(ftsIndex)
    }

}