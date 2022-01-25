package me.elizabethlfransen.music4k

import org.slf4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfig {
    @Bean
    fun run(logger: Logger, properties: DiscordProperties) = CommandLineRunner {
        logger.info("Token: {}", properties.token)
    }
}