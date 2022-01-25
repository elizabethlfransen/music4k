package me.elizabethlfransen.music4k.commands.core

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import org.reactivestreams.Publisher

interface Command {
    val spec: ApplicationCommandRequest
    fun execute(event: ChatInputInteractionEvent): Publisher<*>
}