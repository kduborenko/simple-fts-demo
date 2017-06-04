package net.duborenko.fts

import net.duborenko.fts.model.Document
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.util.UUID

/**
 * @author Kiryl Dubarenka
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
open class Configuration {

    @Bean open fun ftsIndex() = FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>()

}