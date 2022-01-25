package me.elizabethlfransen.music4k.player

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.util.concurrent.LinkedBlockingQueue

@Service
@Scope("singleton")
final class TrackScheduler(private val player: AudioPlayer) : AudioEventAdapter() {
    init {
        player.addListener(this)
    }

    private val queue = LinkedBlockingQueue<AudioTrack>()
    private var currentTrack: AudioTrack? = null

    private fun startTrack(track: AudioTrack, noInterrupt: Boolean) {
        if(player.startTrack(track, noInterrupt))
            currentTrack = track
        else
            queue.offer(track)
    }

    fun queue(info: AudioTrack) {
        startTrack(info, true)
    }


    fun playNextTrack(): AudioTrack? {
        val result = currentTrack
        player.stopTrack()
        currentTrack = null
        queue.poll()?.let{startTrack(it, false)}
        return result
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext)
            playNextTrack()
    }
}