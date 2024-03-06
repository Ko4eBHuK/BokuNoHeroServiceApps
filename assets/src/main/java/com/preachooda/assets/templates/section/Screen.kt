package com.preachooda.assets.templates.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.preachooda.assets.templates.section.domain.Intent
import com.preachooda.assets.templates.section.viewModel.ViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog

@Composable
fun Screen( // TODO: rename and register screen in navigation
    navController: NavController,
    viewModel: ViewModel
) {
    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(Intent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(Intent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    // TODO: Add your content here

}