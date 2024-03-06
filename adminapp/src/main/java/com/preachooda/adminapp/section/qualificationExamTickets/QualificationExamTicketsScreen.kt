package com.preachooda.adminapp.section.qualificationExamTickets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.adminapp.navigation.Screens
import com.preachooda.adminapp.section.qualificationExamTickets.domain.QualificationExamTicketsIntent
import com.preachooda.adminapp.section.qualificationExamTickets.viewModel.QualificationExamTicketsViewModel
import com.preachooda.assets.R
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.PurpleGrey40
import com.preachooda.assets.ui.theme.StatusAssignedColor
import com.preachooda.assets.ui.theme.StatusCompletedColor
import com.preachooda.assets.ui.theme.StatusDeclinedColor
import com.preachooda.assets.ui.theme.StatusNewColor
import com.preachooda.assets.ui.theme.StatusValuationColor
import com.preachooda.assets.ui.theme.StatusWipColor
import com.preachooda.assets.ui.theme.TextGray
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.QualificationExamApplication

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun QualificationExamTicketsScreen(
    navController: NavController,
    viewModel: QualificationExamTicketsViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) viewModel.processIntent(QualificationExamTicketsIntent.Refresh)
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(QualificationExamTicketsIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(QualificationExamTicketsIntent.CloseMessage) },
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
                    Text("Заявки на экзамен")
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
                viewModel.processIntent(QualificationExamTicketsIntent.Refresh)
            }
        )

        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn { // Tickets
                if (screenState.examApplications.isNotEmpty()) {
                    screenState.examApplications.forEach {
                        item {
                            LicenseTicketItem(data = it) {
                                navController.navigate("${Screens.QualificationExamTicket.route}/${it.id}")
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Нет заявок на квалификационный экзамен.",
                            color = TextGray,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
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
private fun LicenseTicketItem(
    data: QualificationExamApplication,
    onTicketClick: () -> Unit
) {
    ElevatedCard(
        onClick = onTicketClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
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
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(.95f)
            ) {
                Text(
                    text = "№ заявки: ${data.id}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = data.status.value,
                    color = when (data.status) {
                        ActivityStatus.CREATED -> StatusNewColor
                        ActivityStatus.ASSIGNED -> StatusAssignedColor
                        ActivityStatus.IN_WORK -> StatusWipColor
                        ActivityStatus.EVALUATION -> StatusValuationColor
                        ActivityStatus.COMPLETED -> StatusCompletedColor
                        ActivityStatus.REJECTED -> StatusDeclinedColor
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_open),
                contentDescription = "Open ticket",
                tint = PurpleGrey40
            )
        }
    }
}
