package me.elizabethlfransen.music4k.config

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import me.elizabethlfransen.music4k.config.properties.AudioProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class AudioConfig {
    @Bean
    @Scope("singleton")
    fun audioPlayerManager() = DefaultAudioPlayerManager()
        .apply {
            configuration
                .setFrameBufferFactory(::NonAllocatingAudioFrameBuffer)
            AudioSourceManagers.registerRemoteSources(this)
        }
    @Bean
    @Scope("singleton")
    fun player(manager: AudioPlayerManager, audioProperties: AudioProperties): AudioPlayer = manager.createPlayer().apply {
        volume = audioProperties.defaultVolume
    }
}