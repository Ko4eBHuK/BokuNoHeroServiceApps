package com.preachooda.bokunoheroservice.utils

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException

const val LOG_TAG = "AudioHelper"

class AudioHelper {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var _fileName: String = ""

    fun startRecording(userId: String) {
        if (recorder == null) {
            val audioFile = createAudioRecordingFile(userId = userId)
            val filePath = audioFile.absolutePath
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD)
                setAudioEncodingBitRate(16)
                setAudioSamplingRate(44100)
                setOutputFile(filePath)

                try {
                    prepare()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "prepare() failed: ${e.message}")
                }

                start()
                _fileName = audioFile.name
                Log.d(LOG_TAG, "startRecording() file=$_fileName")
            }
        }
    }

    fun stopRecording(): String {
        Log.d(LOG_TAG, "stopRecording() file=$_fileName")
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        return _fileName
    }

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
