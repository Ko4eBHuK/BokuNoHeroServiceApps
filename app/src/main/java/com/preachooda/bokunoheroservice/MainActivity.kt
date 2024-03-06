package com.preachooda.bokunoheroservice

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.preachooda.bokunoheroservice.navigation.SetupNavGraph
import com.preachooda.assets.ui.theme.BokuNoHeroServiceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BokuNoHeroServiceTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    activity = this
                )
            }
        }

        requestPermissions()
    }

    private fun requestPermissions() {
        val permissionsArray = mutableSetOf<String>()

        permissionsArray.addPermissionIfNotGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionsArray.addPermissionIfNotGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsArray.addPermissionIfNotGranted(Manifest.permission.CAMERA)
        permissionsArray.addPermissionIfNotGranted(Manifest.permission.RECORD_AUDIO)

        if (permissionsArray.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsArray.toTypedArray(),
                0
            )
        }
    }

    private fun MutableSet<String>.addPermissionIfNotGranted(permission: String) {
        if (
            ActivityCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.add(permission)
        }
    }
}
