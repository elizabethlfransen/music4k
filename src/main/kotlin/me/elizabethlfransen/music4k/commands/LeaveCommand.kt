package me.elizabethlfransen.music4k.commands

import discord4j.core.`object`.entity.Guild
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import me.elizabethlfransen.music4k.commands.core.Command
import me.elizabethlfransen.music4k.exceptions.CommandException
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class LeaveCommand(
    private val guild: Mono<Guild>
) : Command {
    override val spec: ApplicationCommandRequest
        get() = ApplicationCommandRequest.builder()
            .name("leave")
            .description("Makes the bot disconnect the voice channel")
            .build()

    override fun execute(event: ChatInputInteractionEvent): Publisher<*> {
        return guild
            .flatMap { guild -> guild.voiceConnection }
            .switchIfEmpty { CommandException("Music4K is not currently connected").toMono() }
            .flatMap { vc -> vc.disconnect() }
            .then(Mono.defer { event.reply("Disconnected") })
    }
}