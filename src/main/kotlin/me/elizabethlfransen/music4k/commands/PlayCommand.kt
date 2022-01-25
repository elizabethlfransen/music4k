package me.elizabethlfransen.music4k.commands

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.util.Color
import me.elizabethlfransen.music4k.commands.core.Command
import me.elizabethlfransen.music4k.exceptions.CommandException
import me.elizabethlfransen.music4k.player.PlayerUtil
import me.elizabethlfransen.music4k.player.addTrackInfo
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private const val OPTION_TRACK = "track"

@Service
class PlayCommand(
    private val playerUtil: PlayerUtil
) : Command {

    override val spec: ApplicationCommandRequest
        get() = ApplicationCommandRequest
            .builder()
            .name("play")
            .description("Adds a song to the queue")
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(OPTION_TRACK)
                    .description("The track to add: Either a url or a search term")
                    .type(ApplicationCommandOption.Type.STRING.value)
                    .required(true)
                    .build()
            )
            .build()

    override fun execute(event: ChatInputInteractionEvent): Publisher<*> {
        val trackIdentifier = event.getOption(OPTION_TRACK).get().value.get().asString()
        return playerUtil
            .joinMemberIfNotConnected(event.interaction.member.get())
            .then(playerUtil.queue(trackIdentifier))
            .switchIfEmpty(Mono.error(CommandException("Song not found")))
            .map(AudioTrack::getInfo)
            .flatMap { track ->
                event.reply(
                    InteractionApplicationCommandCallbackSpec.builder()
                        .addEmbed(
                            EmbedCreateSpec.builder()
                                .color(Color.WHITE)
                                .title("Added Song to Queue")
                                .addTrackInfo(track)
                                .build()
                        )
                        .build()
                )
            }
    }
}