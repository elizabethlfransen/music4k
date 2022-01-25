package me.elizabethlfransen.music4k.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix="audio")
@ConstructorBinding
data class AudioProperties(
    val defaultVolume: Int
)
