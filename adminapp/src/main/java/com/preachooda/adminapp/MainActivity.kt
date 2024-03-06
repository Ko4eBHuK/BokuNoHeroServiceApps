package com.preachooda.adminapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.preachooda.adminapp.navigation.SetupNavGraph
import com.preachooda.adminapp.utils.SystemRepository
import com.preachooda.assets.ui.theme.BokuNoHeroServiceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var systemRepository: SystemRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BokuNoHeroServiceTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    activity = this,
                    userLogged = !systemRepository.getUserToken().isNullOrBlank()
                )
            }
        }
    }
}
