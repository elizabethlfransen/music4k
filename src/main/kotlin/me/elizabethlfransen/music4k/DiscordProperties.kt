package me.elizabethlfransen.music4k

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix="discord")
@ConstructorBinding
data class DiscordProperties(
    val token: String?
)
