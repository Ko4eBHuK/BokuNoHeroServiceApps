package com.preachooda.bokunoheroservice.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationRequestCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.gson.Gson
import com.preachooda.bokunoheroservice.R
import com.preachooda.domain.FileNotDeletedException
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.section.newticket.MESSAGE_NEW_TICKET_LOCATION_PERMISSION_NOT_GRANTED
import com.preachooda.bokunoheroservice.section.newticket.NEW_TICKET_MAPVIEW_NO_LOCATION
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    fun saveTicketTemplate(ticket: Ticket) = with(sharedPreferences.edit()) {
        putString(context.getString(R.string.ticket_template_key), Gson().toJson(ticket))
        apply()
    }

    fun getTicketTemplate(): Ticket? = Gson().fromJson(
        sharedPreferences.getString(context.getString(R.string.ticket_template_key), ""),
        Ticket::class.java
    )

    fun deleteTicketTemplate() = with(sharedPreferences.edit()) {
        remove(context.getString(R.string.ticket_template_key))
        apply()
    }

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

    fun getLocationFused(
        onSuccess: (Location) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onFailure(MESSAGE_NEW_TICKET_LOCATION_PERMISSION_NOT_GRANTED)
        } else {
            fusedLocationClient.getCurrentLocation(
                LocationRequestCompat.QUALITY_LOW_POWER,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }
            ).addOnFailureListener {
                onFailure("Получение местоположения не выполнено. ${it.message}")
            }.addOnSuccessListener { location: Location? ->
                if (location == null) {
                    onFailure(NEW_TICKET_MAPVIEW_NO_LOCATION)
                } else {
                    onSuccess(location)
                }
            }
        }
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

    @Throws(FileNotFoundException::class)
    fun getFile(absolutePath: String): File {
        val file = File(absolutePath)
        return if (file.exists()) file else throw FileNotFoundException()
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
