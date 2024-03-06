package com.preachooda.academyapp.section.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.assets.R
import com.preachooda.academyapp.section.application.domain.ApplicationScreenIntent
import com.preachooda.academyapp.section.application.viewModel.AdmissionViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.TextGray
import com.preachooda.domain.model.ActivityStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ApplicationScreen(
    navController: NavController,
    viewModel: AdmissionViewModel,
    applicationId: Long
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.processIntent(ApplicationScreenIntent.Refresh(applicationId))
            }

            else -> { /*do nothing*/
            }
        }
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(ApplicationScreenIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(ApplicationScreenIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = screenState.isLoading,
        onRefresh = {
            viewModel.processIntent(ApplicationScreenIntent.Refresh(applicationId))
        }
    )

    if (screenState.application != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Заявка на поступление №${screenState.application.id}")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryRed,
                        titleContentColor = PrimaryWhite
                    )
                )
            },
            bottomBar = {
                val bottomBarBtnText = when (screenState.application.status) {
                    ActivityStatus.CREATED -> "Взять в работу"
                    ActivityStatus.IN_WORK -> "Завершить обработку"
                    ActivityStatus.EVALUATION -> ""
                    ActivityStatus.COMPLETED -> ""
                    ActivityStatus.REJECTED -> ""
                    ActivityStatus.ASSIGNED -> ""
                }
                if (screenState.application.status == ActivityStatus.CREATED ||
                    screenState.application.status == ActivityStatus.IN_WORK
                ) {
                    BottomAppBar {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            PrimaryButton(
                                onClick = {
                                    viewModel.processIntent(ApplicationScreenIntent.HandleApplication)
                                },
                                text = bottomBarBtnText
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        TitleText(
                            text = "Информация о заявителе",
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                        )
                    }
                    item {
                        ElevatedCard(
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                .fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                contentColor = PrimaryBlack,
                                containerColor = PrimaryWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = "ФИО: ${screenState.application.printedName}",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                            )
                            Text(
                                text = "Возраст: ${screenState.application.age}",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                            )
                            Text(
                                text = "Причуда: ${screenState.application.quirk}",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                            )
                        }
                    }

                    item {
                        TitleText(
                            text = "Сообщение заявителя",
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                        )
                    }
                    item {
                        ElevatedCard(
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                .fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                contentColor = PrimaryBlack,
                                containerColor = PrimaryWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = screenState.application.message,
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                            )
                        }
                    }

                    item {
                        TitleText(
                            text = "Выбранные академии",
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                        )
                    }
                    item {
                        ElevatedCard(
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                .fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                contentColor = PrimaryBlack,
                                containerColor = PrimaryWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = "Приоритет 1: ${screenState.application.firstAcademy.label}",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                            )
                            Text(
                                text = "Приоритет 2: ${screenState.application.secondAcademy.label}",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                            )
                            Text(
                                text = "Приоритет 3: ${screenState.application.thirdAcademy.label}",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
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
    } else {
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
                .background(PrimaryWhite),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Заявление на поступление не прогрузилось :(",
                            modifier = Modifier
                                .fillMaxWidth(),
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
