package com.preachooda.heroapp.section.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.heroapp.R
import com.preachooda.heroapp.navigation.Screens
import com.preachooda.heroapp.section.login.domain.AuthenticateIntent
import com.preachooda.heroapp.section.login.domain.CloseErrorIntent
import com.preachooda.heroapp.section.login.domain.LoginStatus
import com.preachooda.heroapp.section.login.viewModel.LoginViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.fontDimensionResource
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.RedAlpha80

const val fieldWidth = 300

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                when (viewModel.defineLoginStatus()) {
                    LoginStatus.LOGGED -> {
                        navController.navigate(Screens.Home.route) {
                            popUpTo(Screens.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                    LoginStatus.NOT_LOGGED -> {
                        // do nothing
                    }
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    val screenState = viewModel.screenState.collectAsState().value
    if (screenState.authComplete) {
        when (viewModel.defineLoginStatus()) {
            LoginStatus.LOGGED -> {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Login.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            LoginStatus.NOT_LOGGED -> {
                // do nothing
            }
        }
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var usernameValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = {
                viewModel.processIntent(CloseErrorIntent)
            },
            dialogText = screenState.message
        )
    }
    if (screenState.isShowMessage) {
        ConfirmAlertDialog(
            onDismissRequest = {
                viewModel.processIntent(CloseErrorIntent)
            },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(dialogText = screenState.message)
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(PrimaryWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.login_screen_username_title),
                modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = fontDimensionResource(id = com.preachooda.assets.R.dimen.default_title_text_size),
                color = PrimaryRed
            )
            OutlinedTextField(
                value = usernameValue,
                onValueChange = {
                    usernameValue = it
                },
                modifier = Modifier
                    .width(fieldWidth.dp)
                    .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding))
                    .focusRequester(focusRequester),
                maxLines = 1,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = PrimaryRed,
                    focusedContainerColor = PrimaryWhite,
                    unfocusedContainerColor = PrimaryWhite
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            var showPassword by remember { mutableStateOf(false) }
            Text(
                text = stringResource(id = R.string.login_screen_password_title),
                modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = fontDimensionResource(id = com.preachooda.assets.R.dimen.default_title_text_size),
                color = PrimaryRed
            )
            OutlinedTextField(
                value = passwordValue,
                onValueChange = {
                    passwordValue = it
                },
                modifier = Modifier
                    .width(fieldWidth.dp)
                    .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding))
                    .focusRequester(focusRequester),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            painter = if (!showPassword) painterResource(id = R.drawable.ic_eye)
                            else painterResource(id = R.drawable.ic_eye_crossed),
                            contentDescription = "password visibility",
                            tint = RedAlpha80
                        )
                    }
                },
                visualTransformation = if (!showPassword) PasswordVisualTransformation()
                else VisualTransformation.None,
                maxLines = 1,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = PrimaryRed,
                    focusedContainerColor = PrimaryWhite,
                    unfocusedContainerColor = PrimaryWhite
                )
            )

            Spacer(modifier = Modifier.height(60.dp))

            PrimaryButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.processIntent(
                        AuthenticateIntent(
                            username = usernameValue,
                            password = passwordValue
                        )
                    )
                },
                text = stringResource(id = R.string.login_screen_login_btn_text),
                modifier = Modifier
                    .width(fieldWidth.dp)
            )
        }
    }
}
