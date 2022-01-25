package me.elizabethlfransen.music4k

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(DiscordProperties::class)
class Music4kApplication

fun main(args: Array<String>) {
    runApplication<Music4kApplication>(*args)
}
