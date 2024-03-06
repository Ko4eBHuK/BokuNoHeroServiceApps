package com.preachooda.heroapp.section.patrolling

import androidx.compose.foundation.background
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.assets.R
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ConfirmCancelAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.fontDimensionResource
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.SecondaryBlack
import com.preachooda.domain.model.Patrol
import com.preachooda.heroapp.section.patrolling.domain.PatrollingScreenIntent
import com.preachooda.heroapp.section.patrolling.viewModel.PatrollingViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PatrollingScreen(
    navController: NavController,
    viewModel: PatrollingViewModel
) {
    val screenState = viewModel.screenState.collectAsState().value
    var showSendDialog by remember { mutableStateOf(false) }

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(PatrollingScreenIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(PatrollingScreenIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }
    if (showSendDialog) {
        screenState.patrol?.let { patrol ->
            val text = when (patrol.status) {
                Patrol.Status.PENDING -> "Начать патрулирование?"
                Patrol.Status.STARTED -> "Завершить патрулирование?"
                Patrol.Status.COMPLETED -> "Патрулирование завершено и находится в конечном статусе, отправить?"
            }
            ConfirmCancelAlertDialog(
                dialogText = text,
                confirmRequest = {
                    when (patrol.status) {
                        Patrol.Status.PENDING -> viewModel.processIntent(PatrollingScreenIntent.StartPatrolling)
                        Patrol.Status.STARTED -> viewModel.processIntent(PatrollingScreenIntent.FinishPatrolling)
                        Patrol.Status.COMPLETED -> viewModel.processIntent(
                            PatrollingScreenIntent.ShowError(
                                "Патрулирование уже завершено"
                            )
                        )
                    }
                    showSendDialog = false
                },
                cancelRequest = { showSendDialog = false }
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.processIntent(PatrollingScreenIntent.Refresh)
            }
            else -> { /*do nothing*/ }
        }
    }

    if (screenState.patrol!= null) {
        Scaffold(
            topBar = {
                PatrollingTopBar(screenState.patrol)
            },
            bottomBar = {
                val sendBtnText = when (screenState.patrol.status) {
                    Patrol.Status.PENDING -> "Начать"
                    Patrol.Status.STARTED -> "Завершить"
                    Patrol.Status.COMPLETED -> ":("
                }
                PatrollingBottomBar(
                    buttonClick = {
                        showSendDialog = true
                    },
                    sendBtnText
                )
            }
        ) { paddingValues ->
            val pullRefreshState = rememberPullRefreshState(
                refreshing = screenState.isLoading,
                onRefresh = {
                    viewModel.processIntent(PatrollingScreenIntent.Refresh)
                }
            )

            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn {
                    item( // District
                        key = screenState.patrol.district
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                contentColor = PrimaryBlack,
                                containerColor = PrimaryWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            )
                        ) {
                            TitleText(
                                text = "Район патрулирования",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                            Text(
                                text = screenState.patrol.district,
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                        }
                    }

                    item { // Schedule
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice)),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                contentColor = PrimaryBlack,
                                containerColor = PrimaryWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            )
                        ) {
                            TitleText(
                                text = "Расписание",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                            Text(
                                text = "Начало: ${screenState.patrol.scheduledStart}" +
                                        "\nЗавершение: ${screenState.patrol.scheduledEnd}",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                        }
                    }

                    if (screenState.patrol.heroes.isNotEmpty()) { // heroes info
                        item(
                            key = screenState.patrol.heroes
                        ) {
                            Column {
                                TitleText(
                                    text = "Герои",
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                )
                                screenState.patrol.heroes.forEach { hero ->
                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            contentColor = PrimaryBlack,
                                            containerColor = PrimaryWhite
                                        ),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 5.dp
                                        )
                                    ) {
                                        Text(
                                            text = "${hero.label}\n" +
                                                    "Место в рейтинге: ${hero.rankingPosition}\n" +
                                                    "Владение причудой: ${hero.skillByQuirk}",
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
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
    } else {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(PatrollingScreenIntent.Refresh)
            }
        )
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
                .background(PrimaryWhite),
            contentAlignment = Alignment.Center
        ) {
            PullRefreshIndicator(
                refreshing = screenState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = PrimaryRed
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(screenHeight),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Патруль не назначен.",
                            modifier = Modifier.fillMaxWidth(.8f),
                            fontSize = fontDimensionResource(id = R.dimen.default_title_text_size),
                            color = SecondaryBlack,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatrollingTopBar(
    patrol: Patrol
) {
    TopAppBar(
        title = {
            Text("Патрулирование №${patrol.id}")
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryRed,
            titleContentColor = PrimaryWhite
        )
    )
}

@Composable
private fun PatrollingBottomBar(
    buttonClick: () -> Unit,
    buttonText: String,
) {
    BottomAppBar {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                onClick = buttonClick,
                text = buttonText
            )
        }
    }
}
