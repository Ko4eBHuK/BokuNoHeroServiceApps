package com.preachooda.academyapp.utils

import android.content.Context
import com.preachooda.academyapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
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
}