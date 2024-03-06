package com.preachooda.heroapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.preachooda.assets.ui.theme.BokuNoHeroServiceTheme
import com.preachooda.heroapp.navigation.SetupNavGraph
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
