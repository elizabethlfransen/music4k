package me.elizabethlfransen.music4k.player

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.VoiceChannelJoinSpec
import discord4j.voice.AudioProvider
import discord4j.voice.VoiceConnection
import me.elizabethlfransen.music4k.exceptions.CommandException
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
@Scope("singleton")
class PlayerUtil(
    private val scheduler: TrackScheduler,
    private val manager: AudioPlayerManager,
    private val provider: AudioProvider,
    private val guild: Mono<Guild>
) {
    fun isConnected(): Mono<Boolean> {
        return guild
            .flatMap { g -> g.voiceConnection }
            .hasElement()
    }

    fun joinMemberIfNotConnected(member: Member): Mono<Pair<VoiceChannel, VoiceConnection>> {
        return isConnected()
            .flatMap { connected ->
                if(connected)
                    Mono.empty()
                else
                    joinMember(member)
            }
    }

    fun joinMember(member: Member): Mono<Pair<VoiceChannel, VoiceConnection>> {
        return member.voiceState
            .flatMap { vs -> vs.channel }
            .switchIfEmpty { CommandException("You are not connected to a voice channel").toMono() }
            .flatMap { c ->
                c
                    .join(
                        VoiceChannelJoinSpec.builder()
                            .provider(provider)
                            .build()
                    )
                    .map { vc -> c to vc }
            }
    }

    fun queue(identifier: String): Mono<AudioTrack> {
        return Mono.create { resolver ->
            manager.loadItem(identifier, (object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    scheduler.queue(track)
                    resolver.success(track)
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    if (!playlist.isSearchResult)
                        resolver.error(IllegalArgumentException("Playlists are not yet supported"))
                    val track = playlist
                        .selectedTrack
                        ?: playlist.tracks.firstOrNull()
                    if (track != null)
                        trackLoaded(track)
                    noMatches()
                }

                override fun noMatches() {
                    resolver.success()
                }

                override fun loadFailed(exception: FriendlyException) {
                    resolver.error(exception)
                }
            }))
        }
    }
}

fun EmbedCreateSpec.Builder.addTrackInfo(track: AudioTrackInfo): EmbedCreateSpec.Builder {
    return this
        .addField("Title", track.title, false)
        .addField("Author", track.author, false)
        .url(track.uri)
}