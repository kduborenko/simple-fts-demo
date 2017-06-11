package net.duborenko.fts

import net.duborenko.fts.model.Document
import org.springframework.boot.SpringApplication
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
open class Application {

    @Bean open fun ftsIndex() =
            FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>(
                    rank = { it.matches.values.sumBy { it.size } }
            )

    companion object {
        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(arrayOf(Application::class.java, AwsConfiguration::class.java), args)
        }
    }

}
