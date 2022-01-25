package me.elizabethlfransen.music4k.commands

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import me.elizabethlfransen.music4k.commands.core.Command
import me.elizabethlfransen.music4k.exceptions.CommandException
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class JoinCommand : Command {
    override val spec: ApplicationCommandRequest
        get() = ApplicationCommandRequest.builder()
            .name("join")
            .description("Joins the current voice channel")
            .build()

    override fun execute(event: ChatInputInteractionEvent): Publisher<*> {
        return event.interaction.member.get().voiceState
            .switchIfEmpty { CommandException("You are not connected to a voice channel").toMono() }
            .flatMap { vs -> vs.channel }
            .flatMap { c -> c.join().map { c} }
            .flatMap { event.reply("Joined ${it.name}") }
    }
}