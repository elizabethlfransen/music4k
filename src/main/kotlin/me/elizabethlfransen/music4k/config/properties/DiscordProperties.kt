package me.elizabethlfransen.music4k.config.properties

import discord4j.common.util.Snowflake
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix="discord")
@ConstructorBinding
data class DiscordProperties(
    val token: String,
    val guild: Snowflake
)
