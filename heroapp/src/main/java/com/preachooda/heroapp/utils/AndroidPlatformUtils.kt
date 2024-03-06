package com.preachooda.heroapp.utils

import android.annotation.SuppressLint
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date

fun getPicturesPublicDirectory(): File {
    val imgDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "HeroApp/"
    )
    if (!imgDir.exists()) imgDir.mkdirs()
    return imgDir
}

@SuppressLint("SimpleDateFormat")
fun createImageFile(fileName: String = ""): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_.jpg"
    return File(
        getPicturesPublicDirectory(),
        fileName.ifBlank { imageFileName }
    )
}

fun getImageFileByNameFromPublicDirectory(fileName: String): File? {
    val imgFile = File(
        getPicturesPublicDirectory(),
        fileName
    )
    return if (imgFile.exists()) imgFile else null
}

@Throws(Exception::class)
fun saveBase64ToImageFile(base64Str: String, fileName: String) {
    val bytesToWrite = Base64.getDecoder().decode(base64Str)
    val file = createImageFile(fileName)
    file.writeBytes(bytesToWrite)
}


fun getVideosPublicDirectory(): File {
    val appVideoDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
        "HeroApp/"
    )
    if (!appVideoDir.exists()) appVideoDir.mkdirs()
    return appVideoDir
}

@SuppressLint("SimpleDateFormat")
fun createVideoFile(fileName: String = ""): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val videoFileName = "MP4_" + timeStamp + "_.mp4"
    return File(
        getVideosPublicDirectory(),
        fileName.ifBlank { videoFileName }
    )
}

fun getVideoFileByNameFromPublicDirectory(fileName: String): File? {
    val videoFile = File(
        getVideosPublicDirectory(),
        fileName
    )
    return if (videoFile.exists()) videoFile else null
}

fun saveBase64ToVideoFile(base64Str: String, fileName: String) {
    val bytesToWrite = Base64.getDecoder().decode(base64Str)
    val file = createVideoFile(fileName)
    file.writeBytes(bytesToWrite)
}


fun getAudiosPublicDirectory(): File {
    val appAudioDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "HeroApp/"
    )
    if (!appAudioDir.exists()) appAudioDir.mkdirs()
    return appAudioDir
}

@SuppressLint("SimpleDateFormat")
fun createAudioRecordingFile(fileName: String = ""): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val audioFileName = "recording_" + timeStamp + "_.mp3"
    return File(
        getAudiosPublicDirectory(),
        fileName.ifBlank { audioFileName }
    )
}

fun getAudioFileByNameFromPublicDirectory(fileName: String): File? {
    val audioFile = File(
        getAudiosPublicDirectory(),
        fileName,
    )
    return if (audioFile.exists()) audioFile else null
}

fun saveBase64ToAudioFile(base64Str: String, fileName: String) {
    val bytesToWrite = Base64.getDecoder().decode(base64Str)
    val file = createAudioRecordingFile(fileName)
    file.writeBytes(bytesToWrite)
}
