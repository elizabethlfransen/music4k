package me.elizabethlfransen.music4k.config

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.ApplicationInfo
import discord4j.core.`object`.entity.Guild
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandData
import discord4j.discordjson.json.ApplicationCommandRequest
import me.elizabethlfransen.music4k.commands.core.Command
import me.elizabethlfransen.music4k.exceptions.CommandException
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Configuration
class CommandRegistryConfig {
    private val logger = LoggerFactory.getLogger(CommandRegistryConfig::class.java)

    private fun register(
        command: ApplicationCommandRequest,
        client: GatewayDiscordClient,
        guild: Guild
    ): Mono<ApplicationCommandData> {
        return client.applicationInfo
            .map(ApplicationInfo::getId)
            .map(Snowflake::asLong)
            .flatMap { id ->
                client.restClient.applicationService.createGuildApplicationCommand(
                    id,
                    guild.id.asLong(),
                    command
                )
            }
            .onErrorResume { error ->
                logger.error("Unable to create command ${command.name()}", error)
                Mono.empty()
            }
    }

    private fun register(commands: Set<Command>, client: GatewayDiscordClient, guild: Guild): Mono<Void> {
        return commands
            .map { command -> register(command.spec, client, guild) }
            .map(Mono<*>::then)
            .let { Mono.`when`(it) }
    }

    fun executeCommand(command: Command, event: ChatInputInteractionEvent): Mono<Void> {
        return command.execute(event)
            .toMono()
            .then()
            .onErrorResume(CommandException::class.java) {
                event.reply(it.message!!)
            }
            .doOnError {
                logger.error("Error executing command ${command.spec.name()}", it)
            }
    }

    @Bean
    fun register(commands: Set<Command>, client: GatewayDiscordClient, guild: Mono<Guild>) = CommandLineRunner {
        val commandMap = commands.associateBy { cmd -> cmd.spec.name() }
        guild
            .flatMap { guild ->
                Mono.just(guild)
            }
            .flatMap { guild -> register(commands, client, guild) }
            .doOnSuccess {
                logger.info("Commands Finished Registering")
            }
            .subscribe()
        client.on(ChatInputInteractionEvent::class.java)
            .flatMap { event ->
                commandMap[event.commandName]
                    ?.let { command -> executeCommand(command, event) }
                    ?: Mono.empty()
            }
            .subscribe()
    }
}