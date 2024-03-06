package com.preachooda.academyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.preachooda.academyapp.navigation.SetupNavGraph
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
    }
}
