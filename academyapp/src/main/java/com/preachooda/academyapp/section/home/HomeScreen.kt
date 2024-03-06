package com.preachooda.academyapp.section.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ConfirmCancelAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.PurpleGrey40
import com.preachooda.academyapp.MainActivity
import com.preachooda.academyapp.R
import com.preachooda.academyapp.navigation.Screens
import com.preachooda.academyapp.section.home.domain.HomeScreenIntent
import com.preachooda.academyapp.section.home.viewModel.HomeViewModel

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
            dialogText = "Выйти из учётной записи?",
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

            ElevatedCard( // open AcademyApplications
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
                    navController.navigate(Screens.Applications.route)
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
                    Text(text = "Заявки на поступление")
                    Icon(
                        painter = painterResource(id = com.preachooda.assets.R.drawable.ic_arrow_open),
                        contentDescription = "Icon arrow open",
                        tint = PurpleGrey40
                    )
                }
            }

            ElevatedCard( // open LicenseCreation
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
                    navController.navigate(Screens.LicenseCreation.route)
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
                    Text(text = "Заявка на лицензию героя")
                    Icon(
                        painter = painterResource(id = com.preachooda.assets.R.drawable.ic_arrow_open),
                        contentDescription = "Icon arrow open",
                        tint = PurpleGrey40
                    )
                }
            }

            ElevatedCard( // open QualificationExam
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
                    navController.navigate(Screens.QualificationExam.route)
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
                    Text(text = "Заявка на квалификационный экзамен")
                    Icon(
                        painter = painterResource(id = com.preachooda.assets.R.drawable.ic_arrow_open),
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
            text = "Геройская Академия"
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
