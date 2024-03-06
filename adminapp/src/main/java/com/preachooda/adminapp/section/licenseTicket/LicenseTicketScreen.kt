package ccom.preachooda.adminapp.section.licenseTicket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.adminapp.section.licenseTicket.domain.LicenseTicketIntent
import com.preachooda.adminapp.section.licenseTicket.viewModel.LicenseTicketViewModel
import com.preachooda.assets.R
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ConfirmButton
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.SecondaryBlack
import com.preachooda.domain.model.ActivityStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LicenseTicketScreen(
    navController: NavController,
    viewModel: LicenseTicketViewModel,
    ticketId: Long
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) viewModel.processIntent(
            LicenseTicketIntent.Refresh(ticketId)
        )
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(LicenseTicketIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(LicenseTicketIntent.CloseMessage) },
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
                    Text("Заявка на лицензию №$ticketId")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryRed,
                    titleContentColor = PrimaryWhite
                )
            )
        },
        bottomBar = {
            if (screenState.licenseApplication?.status == ActivityStatus.CREATED) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ConfirmButton(
                            onClick = {
                                viewModel.processIntent(LicenseTicketIntent.HandleApplication(true))
                            },
                            text = "Подтвердить"
                        )
                        PrimaryButton(
                            onClick = {
                                viewModel.processIntent(LicenseTicketIntent.HandleApplication(false))
                            },
                            text = "Отклонить"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(
                    LicenseTicketIntent.Refresh(ticketId)
                )
            }
        )

        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            val actionZoneHeight = LocalConfiguration.current.screenHeightDp.dp - paddingValues.calculateTopPadding() - paddingValues.calculateBottomPadding()
            LazyColumn {
                if (screenState.licenseApplication != null) {
                    item { // hero
                        Column {
                            TitleText(
                                text = "Информация о заявителе",
                                modifier = Modifier.padding(dimensionResource(R.dimen.default_padding_twice))
                            )
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = "ФИО:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = screenState.licenseApplication.printedName,
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = "Геройский псевдоним:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = screenState.licenseApplication.heroName,
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = "Наименование причуды:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = screenState.licenseApplication.quirk,
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = "Дата рождения:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = screenState.licenseApplication.birthDate,
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = "Документ об образовании:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = screenState.licenseApplication.educationDocumentNumber,
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(actionZoneHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Не удалось загрузить данные заявки",
                                color = SecondaryBlack,
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
