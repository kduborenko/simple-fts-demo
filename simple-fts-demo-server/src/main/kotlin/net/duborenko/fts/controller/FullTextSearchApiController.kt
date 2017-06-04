package net.duborenko.fts.controller

import net.duborenko.fts.FullTextSearchIndex
import net.duborenko.fts.model.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

/**
 * @author Kiryl Dubarenka
 */
@RestController
@RequestMapping("/fts")
class FullTextSearchApiController {

    @Autowired lateinit var ftsIndex: FullTextSearchIndex<UUID, Document>

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun add(@RequestBody document: Document, uriBuilder: UriComponentsBuilder): ResponseEntity<Void> {
        ftsIndex.add(document)

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
    fun search(@RequestParam("search") search: String) = ftsIndex.search(search)

    @RequestMapping(method = arrayOf(RequestMethod.DELETE), value = "/{id}")
    fun search(@PathVariable("id") id: UUID) = ftsIndex.remove(id)

}