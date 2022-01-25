package me.elizabethlfransen.music4k.commands

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandRequest
import me.elizabethlfransen.music4k.commands.core.Command
import me.elizabethlfransen.music4k.player.PlayerUtil
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service

@Service
class JoinCommand(
    private val playerUtil: PlayerUtil
) : Command {
    override val spec: ApplicationCommandRequest
        get() = ApplicationCommandRequest.builder()
            .name("join")
            .description("Joins the current voice channel")
            .build()

    override fun execute(event: ChatInputInteractionEvent): Publisher<*> {
        return playerUtil.joinMember(event.interaction.member.get())
            .map { it.first }
            .flatMap { event.reply("Joined ${it.name}") }
    }
}