package net.duborenko.fts

import org.springframework.boot.SpringApplication

/**
 * @author Kiryl Dubarenka
 */
object Application {

    @Throws(Exception::class)
    @JvmStatic fun main(args: Array<String>) {
        SpringApplication.run(Configuration::class.java, *args)
    }

}
