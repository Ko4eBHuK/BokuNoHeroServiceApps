package com.preachooda.adminapp.section.licenseRecall

import androidx.compose.foundation.background
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.adminapp.section.licenseRecall.domain.LicenseRecallIntent
import com.preachooda.adminapp.section.licenseRecall.viewModel.LicenseRecallViewModel
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
import com.preachooda.domain.model.Hero

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LicenseRecallScreen(
    navController: NavController,
    viewModel: LicenseRecallViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) viewModel.processIntent(LicenseRecallIntent.Refresh)
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(LicenseRecallIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(LicenseRecallIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    var selectedHero: Hero? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Отозвать лицензию")
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
                            viewModel.processIntent(LicenseRecallIntent.RecallIntent(selectedHero))
                        },
                        text = "Отозвать"
                    )
                }
            }
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(
                    LicenseRecallIntent.Refresh
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
                item { // hero
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.default_padding_twice))
                            .height(actionZoneHeight),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TitleText(text = "Герой")
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
                            var expandedHeroDropdown by remember { mutableStateOf(false) }
                            var selectedHeroInfo by remember { mutableStateOf("") }
                            ExposedDropdownMenuBox( // Выбор академии 1
                                expanded = expandedHeroDropdown,
                                onExpandedChange = {
                                    expandedHeroDropdown = !expandedHeroDropdown
                                },
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                    .background(PrimaryWhite)
                            ) {
                                OutlinedTextField( // Выбор академии
                                    value = selectedHeroInfo,
                                    onValueChange = {
                                        selectedHeroInfo = it
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    maxLines = 1,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedHeroDropdown
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryRed,
                                        unfocusedBorderColor = PrimaryGray,
                                        focusedContainerColor = PrimaryWhite,
                                        unfocusedContainerColor = PrimaryWhite
                                    )
                                )

                                val filteredHeroes = screenState.heroes.filter { hero ->
                                    val heroInfo = "${hero.label}, ${hero.quirk}," +
                                            " ${hero.quirkType}, ${hero.skillByQuirk}," +
                                            " рейтинг ${hero.rating}, id ${hero.id}"

                                    heroInfo.contains(selectedHeroInfo, true)
                                }
                                if (filteredHeroes.isNotEmpty()) {
                                    ExposedDropdownMenu(
                                        expanded = expandedHeroDropdown,
                                        onDismissRequest = {
                                            expandedHeroDropdown = false
                                        },
                                    ) {
                                        filteredHeroes.forEach { hero ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = "${hero.label}, ${hero.quirk}," +
                                                                " ${hero.quirkType}, ${hero.skillByQuirk}," +
                                                                " рейтинг ${hero.rating}, id ${hero.id}"
                                                    )
                                                },
                                                onClick = {
                                                    selectedHero = hero
                                                    selectedHeroInfo = ""
                                                    expandedHeroDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            selectedHero?.let { hero ->
                                SelectedHeroItem(
                                    hero = hero,
                                    removeHeroClick = { selectedHero = null },
                                )
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
}

@Composable
private fun SelectedHeroItem(
    hero: Hero,
    removeHeroClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .padding(dimensionResource(id = R.dimen.default_padding)),
        ) {
            Text(text = "Герой: ${hero.label}", fontWeight = FontWeight.SemiBold)
            Text(text = "Причуда: ${hero.quirk}, ${hero.quirkType}, ${hero.skillByQuirk}")
            Text(text = "Инфо: id ${hero.id}, рейтинг ${hero.rating}")
        }

        IconButton(
            onClick = removeHeroClick,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.default_padding)),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete_bin),
                contentDescription = "Delete hero",
                tint = PrimaryRed
            )
        }
    }
}
