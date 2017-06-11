package net.duborenko.fts

import org.springframework.cloud.aws.context.config.annotation.EnableContextCredentials
import org.springframework.cloud.aws.context.config.annotation.EnableContextRegion
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * @author Kiryl Dubarenka
 */
@Configuration
@Profile(AwsConfiguration.AWS_PROFILE)
@EnableContextCredentials(
        accessKey = "#{systemEnvironment.AWS_ACCESS_KEY}",
        secretKey = "#{systemEnvironment.AWS_SECRET_KEY}"
)
@EnableContextRegion(
        region = "\${cloud.aws.region.static}"
)
@EnableContextResourceLoader
open class AwsConfiguration {

    companion object {
        const val AWS_PROFILE = "aws"
    }
}