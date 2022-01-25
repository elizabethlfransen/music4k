package me.elizabethlfransen.music4k.config.converter

import discord4j.common.util.Snowflake
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
@ConfigurationPropertiesBinding
class SnowflakeConverter : Converter<String,Snowflake> {
    override fun convert(source: String): Snowflake {
        return Snowflake.of(source)
    }
}