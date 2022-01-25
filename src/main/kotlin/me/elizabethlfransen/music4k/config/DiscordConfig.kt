package me.elizabethlfransen.music4k.config

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import me.elizabethlfransen.music4k.config.properties.DiscordProperties
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class DiscordConfig {
    @Bean
    @Scope("singleton")
    fun discordClient(properties: DiscordProperties) = DiscordClient.create(properties.token)
        .login()
        .block()!!

    @Bean
    @Scope
    fun guild(client: GatewayDiscordClient, properties: DiscordProperties) = client.getGuildById(properties.guild)

    @Bean
    fun appId(client: GatewayDiscordClient) = client.applicationInfo.map { info -> info.id.asLong() }

    @Bean
    fun run(client: GatewayDiscordClient) = CommandLineRunner {
        client
            .onDisconnect()
            .block()
    }
}