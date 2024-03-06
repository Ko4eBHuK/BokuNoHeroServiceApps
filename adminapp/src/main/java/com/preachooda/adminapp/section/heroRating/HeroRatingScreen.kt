package com.preachooda.adminapp.section.heroRating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.adminapp.section.heroRating.domain.HeroRatingIntent
import com.preachooda.adminapp.section.heroRating.viewModel.HeroRatingViewModel
import com.preachooda.assets.R
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryGray
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HeroRatingScreen(
    navController: NavController,
    viewModel: HeroRatingViewModel,
    heroId: Long
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) viewModel.processIntent(
            HeroRatingIntent.Refresh(
                heroId
            )
        )
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(HeroRatingIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(HeroRatingIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    var newRating by remember { mutableFloatStateOf(-1.0f) }
    screenState.hero?.let { if (newRating < 0) newRating = it.rating }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Рейтинг героя $heroId")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryRed,
                    titleContentColor = PrimaryWhite
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PrimaryButton(
                        onClick = {
                            viewModel.processIntent(HeroRatingIntent.Submit(newRating))
                        },
                        text = "Отправить"
                    )
                }
            }
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(HeroRatingIntent.Refresh(heroId))
            }
        )

        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            val actionZoneHeight =
                LocalConfiguration.current.screenHeightDp.dp - paddingValues.calculateTopPadding() - paddingValues.calculateBottomPadding()

            LazyColumn {
                if (screenState.hero != null) {
                    item {
                        Column {
                            TitleText(
                                text = "Информация о герое",
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
                                        text = "Имя героя:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = screenState.hero.label,
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
                                        text = "Причуда:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${screenState.hero.quirk}, ${screenState.hero.quirkType}, ${screenState.hero.skillByQuirk}",
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
                                        text = "Показатели:",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding)),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "сила ${screenState.hero.strength}, " +
                                                "скорость ${screenState.hero.speed}, " +
                                                "техника ${screenState.hero.technique}, " +
                                                "интеллект ${screenState.hero.intelligence}, " +
                                                "работа в команде ${screenState.hero.cooperation}",
                                        modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = newRating.toString(),
                            onValueChange = {
                                when (it.toFloatOrNull()) {
                                    null -> {}
                                    else -> { newRating = it.toFloat() }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                .semantics { contentDescription = "new rating" },
                            label = {
                                Text(
                                    text = "Новый рейтинг",
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryRed,
                                unfocusedBorderColor = PrimaryGray,
                                focusedContainerColor = PrimaryWhite,
                                unfocusedContainerColor = PrimaryWhite
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
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
                                text = "Не удалось загрузить информацию о герое",
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