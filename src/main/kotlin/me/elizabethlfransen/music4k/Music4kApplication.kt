package me.elizabethlfransen.music4k

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class Music4kApplication

fun main(args: Array<String>) {
    runApplication<Music4kApplication>(*args)
}
