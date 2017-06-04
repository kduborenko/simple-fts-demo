package net.duborenko.fts.model

import net.duborenko.fts.FtsId
import net.duborenko.fts.FtsIndexed
import java.util.UUID

/**
 * @author Kiryl Dubarenka
 */
data class Document(
        @FtsId val id: UUID = UUID.randomUUID(),
        @FtsIndexed val text: String
)