package me.elizabethlfransen.music4k.commands

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec
import discord4j.discordjson.json.ApplicationCommandRequest
import me.elizabethlfransen.music4k.commands.core.Command
import me.elizabethlfransen.music4k.exceptions.CommandException
import me.elizabethlfransen.music4k.player.TrackScheduler
import me.elizabethlfransen.music4k.player.addTrackInfo
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class SkipCommand(private val scheduler: TrackScheduler) : Command {
    override val spec: ApplicationCommandRequest = ApplicationCommandRequest.builder()
        .name("skip")
        .description("Skips the current song")
        .build()

    override fun execute(event: ChatInputInteractionEvent): Publisher<*> {
        return Mono.justOrEmpty(scheduler.playNextTrack())
            .switchIfEmpty(CommandException("The queue is empty").toMono())
            .flatMap { track ->
                event.reply(
                    InteractionApplicationCommandCallbackSpec.builder()
                        .addEmbed(
                            EmbedCreateSpec.builder()
                                .title("Removed song from queue")
                                .addTrackInfo(track.info)
                                .build()
                        )
                        .build()
                )
            }
    }

}