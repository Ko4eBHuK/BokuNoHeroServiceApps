package com.preachooda.adminapp.utils

import android.content.Context
import android.util.Log
import com.preachooda.adminapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
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

    fun setUserId(userId: Long) = with(sharedPreferences.edit()) {
        putLong(
            context.getString(R.string.user_id_key),
            userId
        )
        apply()
    }

    fun getUserId() = sharedPreferences.getLong(
        context.getString(R.string.user_id_key),
        0
    )

    fun clearUserId() = with(sharedPreferences.edit()) {
        remove(context.getString(R.string.user_id_key))
        apply()
    }

    fun setUserNetworkToken(token: String) = with(sharedPreferences.edit()) {
        putString(
            context.getString(R.string.user_network_token_key),
            token
        )
        apply()
    }

    fun getUserToken() = sharedPreferences.getString(
        context.getString(R.string.user_network_token_key),
        ""
    )

    fun clearUserToken() = with(sharedPreferences.edit()) {
        remove(context.getString(R.string.user_network_token_key))
        apply()
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
