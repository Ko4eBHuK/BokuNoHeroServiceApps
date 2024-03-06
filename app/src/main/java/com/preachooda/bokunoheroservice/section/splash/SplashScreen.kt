package com.preachooda.bokunoheroservice.section.splash

import android.content.Intent
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.preachooda.bokunoheroservice.R
import com.preachooda.bokunoheroservice.navigation.Screens
import com.preachooda.bokunoheroservice.section.splash.viewModel.SplashViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel
) {
    val screenState = viewModel.screenState.collectAsState().value
    var showRestartDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val scale = remember {
            androidx.compose.animation.core.Animatable(0.0f)
        }

        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(
                    800,
                    easing = {
                        OvershootInterpolator(4f).getInterpolation(it)
                    }
                )
            )
            delay(1000)
            when (screenState.nextScreen) {
                Screens.Home, Screens.Login -> {
                    navController.navigate(screenState.nextScreen.route) {
                        popUpTo(Screens.Splash.route) {
                            inclusive = true
                        }
                    }
                }
                else -> {
                    showRestartDialog = true
                }
            }
        }

        if (showRestartDialog) {
            ConfirmAlertDialog(
                onDismissRequest = {
                    context.startActivity(
                        Intent.makeRestartActivityTask(
                            context.packageManager
                                .getLaunchIntentForPackage(context.packageName)!!
                                .component
                        )
                    )
                    Runtime.getRuntime().exit(0)
                },
                dialogText = stringResource(id = R.string.splash_screen_restart_dialog_text)
            )
        }

        Image(
            painter = painterResource(com.preachooda.assets.R.drawable.bnh_service_main_no_background),
            contentDescription = "Boku no Hero Service",
            alignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale.value)
        )
    }
}
