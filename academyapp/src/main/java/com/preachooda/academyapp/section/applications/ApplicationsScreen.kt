package com.preachooda.academyapp.section.applications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.academyapp.navigation.Screens
import com.preachooda.academyapp.section.applications.domain.ApplicationsIntent
import com.preachooda.academyapp.section.applications.viewModel.ApplicationsViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.PurpleGrey40
import com.preachooda.assets.ui.theme.SecondaryBlack
import com.preachooda.assets.ui.theme.StatusAssignedColor
import com.preachooda.assets.ui.theme.TextGray
import com.preachooda.assets.ui.theme.StatusCompletedColor
import com.preachooda.assets.ui.theme.StatusDeclinedColor
import com.preachooda.assets.ui.theme.StatusNewColor
import com.preachooda.assets.ui.theme.StatusValuationColor
import com.preachooda.assets.ui.theme.StatusWipColor
import com.preachooda.domain.model.AcademyApplication
import com.preachooda.domain.model.ActivityStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ApplicationsScreen(
    navController: NavController,
    viewModel: ApplicationsViewModel
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.processIntent(ApplicationsIntent.Refresh)
            }

            else -> { /*do nothing*/
            }
        }
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(ApplicationsIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(ApplicationsIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Заявления на поступление")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryRed,
                    titleContentColor = PrimaryWhite
                )
            )
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(ApplicationsIntent.Refresh)
            }
        )
        val screenHeight =
            LocalConfiguration.current.screenHeightDp.dp - paddingValues.calculateTopPadding() - paddingValues.calculateBottomPadding()

        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (screenState.applications.isNotEmpty()) {
                    screenState.applications.forEach {
                        item {
                            ApplicationItem(
                                application = it,
                                onClick = {
                                    navController.navigate("${Screens.Application.route}/${it.id}")
                                }
                            )
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier.height(screenHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Нет заявлений на поступление.",
                                modifier = Modifier.padding(
                                    dimensionResource(id = com.preachooda.assets.R.dimen.default_padding)
                                ),
                                color = TextGray,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = screenState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = PrimaryRed
            )
        }
    }
}

@Composable
private fun ApplicationItem(
    application: AcademyApplication,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            contentColor = PrimaryBlack,
            containerColor = PrimaryWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(.95f)
            ) {
                Text(
                    text = "№ заявления: ${application.id}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Заявитель: ${application.printedName}",
                    color = SecondaryBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = application.status.value,
                    color = when (application.status) {
                        ActivityStatus.CREATED -> StatusNewColor
                        ActivityStatus.IN_WORK -> StatusWipColor
                        ActivityStatus.EVALUATION -> StatusValuationColor
                        ActivityStatus.COMPLETED -> StatusCompletedColor
                        ActivityStatus.REJECTED -> StatusDeclinedColor
                        ActivityStatus.ASSIGNED -> StatusAssignedColor
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                painter = painterResource(id = com.preachooda.assets.R.drawable.ic_arrow_open),
                contentDescription = "Open admission",
                tint = PurpleGrey40
            )
        }
    }
}
