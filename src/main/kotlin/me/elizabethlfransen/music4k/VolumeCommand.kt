package me.elizabethlfransen.music4k

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import me.elizabethlfransen.music4k.commands.core.Command
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service

const val OPTION_VOLUME = "volume"

@Service
class VolumeCommand(
    val player: AudioPlayer
) : Command {
    override val spec: ApplicationCommandRequest
        get() = ApplicationCommandRequest.builder()
            .name("volume")
            .description("Sets the volume of the player")
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(OPTION_VOLUME)
                    .description("The new volume of the player")
                    .required(true)
                    .type(ApplicationCommandOption.Type.INTEGER.value)
                    .minValue(0.0)
                    .maxValue(1000.0)
                    .build()
            )
            .build()

    override fun execute(event: ChatInputInteractionEvent): Publisher<*> {
        player.volume = event.getOption(OPTION_VOLUME).get().value.get().asLong().toInt()
        return event.reply("set Volume to ${player.volume}")
    }
}