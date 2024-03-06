package com.preachooda.adminapp.utils

import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

const val LOG_TAG = "AudioHelper"

class AudioHelper {
    private var player: MediaPlayer? = null

    fun startPlaying(fileName: String, onFinish: () -> Unit) {
        Log.d(LOG_TAG, "startPlaying() file=$fileName")
        player = MediaPlayer().apply {
            try {
                if (isPlaying) {
                    stopPlaying()
                } else {
                    setOnCompletionListener {
                        stopPlaying()
                        onFinish()
                    }
                    setDataSource(getAudioFileByNameFromPublicDirectory(fileName)?.absolutePath ?: "")
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    fun stopPlaying() {
        Log.d(LOG_TAG, "stopPlaying()")
        player?.release()
        player = null
    }
}
