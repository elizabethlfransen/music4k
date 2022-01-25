package me.elizabethlfransen.music4k.commands

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import me.elizabethlfransen.music4k.commands.core.Command
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service

@Service
class OkCommand : Command {
    override val spec: ApplicationCommandRequest = ApplicationCommandRequest.builder()
        .name("ok")
        .description("ok")
        .build()

    override fun execute(event: ChatInputInteractionEvent): Publisher<*> {
        return event.reply("ok")
    }
}