package me.elizabethlfransen.music4k.player

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import discord4j.voice.AudioProvider
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.nio.ByteBuffer

@Service
@Scope("singleton")
class LavaPlayerAudioProvider constructor(private val player: AudioPlayer) : AudioProvider(
    ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())
) {
    private val frame = MutableAudioFrame()
    init {
        frame.setBuffer(buffer)
    }

    override fun provide(): Boolean {
        val didProvide = player.provide(frame)
        if(didProvide)
            buffer.flip()
        return didProvide
    }
}