package com.preachooda.heroapp.utils

import android.content.Context
import android.util.Log
import com.preachooda.domain.FileNotDeletedException
import com.preachooda.heroapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    fun setHeroId(HeroId: Long) = with(sharedPreferences.edit()) {
        putLong(
            context.getString(R.string.hero_id_key),
            HeroId
        )
        apply()
    }

    fun getHeroId() = sharedPreferences.getLong(
        context.getString(R.string.hero_id_key),
        0
    )

    fun clearHeroId() = with(sharedPreferences.edit()) {
        remove(context.getString(R.string.hero_id_key))
        apply()
    }

    fun setHeroNetworkToken(token: String) = with(sharedPreferences.edit()) {
        putString(
            context.getString(R.string.hero_network_token_key),
            token
        )
        apply()
    }

    fun getHeroToken() = sharedPreferences.getString(
        context.getString(R.string.hero_network_token_key),
        ""
    )

    fun clearHeroToken() = with(sharedPreferences.edit()) {
        remove(context.getString(R.string.hero_network_token_key))
        apply()
    }

    @Throws(FileNotDeletedException::class, FileNotFoundException::class)
    fun deleteFile(absolutePath: String) {
        val fileToDelete = File(absolutePath)
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) return
            else throw FileNotDeletedException(absolutePath)
        } else {
            throw FileNotFoundException()
        }
    }

    fun base64ToFile(srcBase64: String, absolutePath: String) {
        val decodedBytes = Base64.getDecoder().decode(srcBase64)
        try {
            val fos = FileOutputStream(absolutePath)
            fos.write(decodedBytes)
            fos.close()
        } catch (e: Exception) {
            Log.e("SystemRepository", "method base64ToMp4File error: ${e.message}", e)
        }
    }
}