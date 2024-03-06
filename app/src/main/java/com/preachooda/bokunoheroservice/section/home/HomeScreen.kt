package com.preachooda.bokunoheroservice.section.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.preachooda.bokunoheroservice.R
import com.preachooda.bokunoheroservice.navigation.Screens
import com.preachooda.bokunoheroservice.section.home.domain.HomeScreenIntent
import com.preachooda.bokunoheroservice.section.home.viewModel.HomeViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ConfirmCancelAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.ServiceButton
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.PurpleGrey40
import com.preachooda.assets.ui.theme.SecondaryBlack
import com.preachooda.bokunoheroservice.MainActivity

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    BackHandler {
        if (!navController.popBackStack()) {
            try {
                (context as MainActivity).finish()
            } catch (e: Exception) {
                // do nothing
            }
        }
    }

    val screenState = viewModel.screenState.collectAsState().value
    if (screenState.isShowMessage) {
        ConfirmAlertDialog(
            onDismissRequest = {
                viewModel.processIntent(HomeScreenIntent.CloseMessageDialog)
            },
            dialogText = screenState.message
        )
    }
    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(HomeScreenIntent.CloseErrorDialog) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(dialogText = screenState.message)
    }
    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        ConfirmCancelAlertDialog(
            dialogText = stringResource(id = R.string.home_screen_logout_dialog_text),
            confirmRequest = {
                viewModel.processIntent(HomeScreenIntent.Logout)
                navController.navigate(Screens.Login.route) {
                    popUpTo(Screens.Home.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            cancelRequest = {
                showLogoutDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                logoutCallback = {
                    showLogoutDialog = true
                }
            )
        },
        bottomBar = {
            HomeBottomBar(
                navController = navController,
                viewModel = viewModel
            )
        }
    ) { contentPadding ->
        // Screen content
        Column(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding()
                )
                .verticalScroll(
                    state = rememberScrollState(),
                ),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(com.preachooda.assets.R.drawable.bnh_service_main_no_background)
                    .build(),
                contentDescription = "logo",
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.Center
            )

            Row( // instructions
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                screenState.instructionsList.forEach {
                    ElevatedCard(
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(240.dp)
                            .padding(5.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            contentColor = PrimaryWhite,
                            containerColor = PrimaryBlack
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp,
                            pressedElevation = 0.dp
                        ),
                        onClick = {
                            // TODO - navigate to instruction screen with provided id
                            Log.d("HomeScreen", "Click on instruction with id ${it.id}")
                        }
                    ) {
                        Text(
                            text = it.label,
                            modifier = Modifier
                                .height(240.dp)
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                    }

                }
            }

            ElevatedCard(
                // open tickets list
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    contentColor = PrimaryBlack,
                    containerColor = PrimaryWhite
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 0.dp
                ),
                onClick = {
                    navController.navigate(Screens.Tickets.route)
                },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Все заявки")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_open),
                        contentDescription = "Icon arrow open",
                        tint = PurpleGrey40
                    )
                }
            }

            ElevatedCard(
                // open tickets list
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    contentColor = PrimaryBlack,
                    containerColor = PrimaryWhite
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 0.dp
                ),
                onClick = {
                    navController.navigate(Screens.AcademyApplication.route)
                },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Заявка на поступление в академию")
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_open),
                        contentDescription = "Icon arrow open",
                        tint = PurpleGrey40
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    logoutCallback: () -> Unit
) = TopAppBar(
    title = {
        Text(
            text = stringResource(id = R.string.home_screen_top_bar_title)
        )
    },
    actions = {
        IconButton(onClick = logoutCallback) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Logout",
                tint = PrimaryWhite
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = PrimaryRed,
        titleContentColor = PrimaryWhite
    )
)

@Composable
private fun HomeBottomBar(
    navController: NavHostController,
    viewModel: HomeViewModel
) = BottomAppBar {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (sosBtn, newTicketBtn) = createRefs()
        PrimaryButton(
            onClick = {
                viewModel.processIntent(HomeScreenIntent.Sos)
            },
            text = stringResource(id = R.string.btn_sos_text),
            modifier = Modifier.constrainAs(sosBtn) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        ServiceButton(
            onClick = {
                navController.navigate(Screens.NewTicket.route)
            },
            modifier = Modifier.constrainAs(newTicketBtn) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "new ticket",
                tint = SecondaryBlack
            )
        }
    }
}
