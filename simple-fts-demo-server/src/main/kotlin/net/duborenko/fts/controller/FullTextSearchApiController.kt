package net.duborenko.fts.controller

import net.duborenko.fts.FullTextSearchIndex
import net.duborenko.fts.model.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

/**
 * @author Kiryl Dubarenka
 */
@RestController("/fts")
class FullTextSearchApiController {

    @Autowired lateinit var ftsIndex: FullTextSearchIndex<Document>

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

}