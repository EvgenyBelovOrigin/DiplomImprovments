package ru.netology.nework.utils

import android.media.MediaPlayer
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

class MediaLifecycleObserver : LifecycleEventObserver {
    private var mediaPlayer: MediaPlayer? = MediaPlayer()


    fun play(trackPath: String) {
        mediaPlayer?.setDataSource(trackPath)

        mediaPlayer?.setOnPreparedListener {
            it.start()
        }
        mediaPlayer?.setOnCompletionListener {
            it.stop()
        }
        mediaPlayer?.prepareAsync()
    }

    fun stop() {
        mediaPlayer?.reset()

    }

    fun pause() {
        mediaPlayer?.pause()

    }

    fun continuePlay() {
        mediaPlayer?.start()
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {

            Lifecycle.Event.ON_PAUSE -> mediaPlayer?.pause()
            Lifecycle.Event.ON_STOP -> {
                mediaPlayer?.release()
            }

            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }

    companion object MediaPlayerManager {
        private val mediaLifecycleObserver: MediaLifecycleObserver = MediaLifecycleObserver()
        val progress = mediaLifecycleObserver.mediaPlayer?.currentPosition
        fun mediaStop() {
            mediaLifecycleObserver.stop()

        }

        fun mediaPlay(trackPath: String) {
            mediaLifecycleObserver.play(trackPath)
        }

        fun mediaPause() {
            mediaLifecycleObserver.pause()
        }

        fun mediaContinuePlay() {
            mediaLifecycleObserver.continuePlay()
        }
    }
}