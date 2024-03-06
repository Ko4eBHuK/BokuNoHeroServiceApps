package com.preachooda.academyapp.section.qualificationExam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.academyapp.section.qualificationExam.domain.QualificationExamIntent
import com.preachooda.academyapp.section.qualificationExam.viewModel.QualificationExamViewModel
import com.preachooda.assets.R
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.theme.PrimaryGray
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.domain.model.Hero

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun QualificationExamScreen(
    navController: NavController,
    viewModel: QualificationExamViewModel
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.processIntent(QualificationExamIntent.Refresh)
            }
            else -> { /*do nothing*/ }
        }
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(QualificationExamIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(QualificationExamIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    var hero: Hero? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Заявление на экзамен")
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
                            viewModel.processIntent(
                                QualificationExamIntent.SendApplication(
                                    hero = hero
                                )
                            )
                        },
                        text = "Подать заявление"
                    )
                }
            }
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(QualificationExamIntent.Refresh)
            }
        )
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp - paddingValues.calculateTopPadding() - paddingValues.calculateBottomPadding()

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
                    Column(
                        modifier = Modifier.height(screenHeight),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var expandedHeroesDropdown by remember { mutableStateOf(false) }
                        var selectedHeroInfo by remember { mutableStateOf("") }
                        TitleText(
                            text = "Выбор героя",
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                        )
                        ExposedDropdownMenuBox( // Выбор академии 1
                            expanded = expandedHeroesDropdown,
                            onExpandedChange = {
                                expandedHeroesDropdown = !expandedHeroesDropdown
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
                                    .menuAnchor()
                                    .semantics { contentDescription = "hero" },
                                maxLines = 1,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedHeroesDropdown
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    unfocusedBorderColor = PrimaryGray,
                                    focusedContainerColor = PrimaryWhite,
                                    unfocusedContainerColor = PrimaryWhite
                                )
                            )

                            val filteredHeroes = screenState.heroes.filter {
                                "${it.label}, ${it.quirk}".contains(selectedHeroInfo, true)
                            }
                            if (filteredHeroes.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = expandedHeroesDropdown,
                                    onDismissRequest = { expandedHeroesDropdown = false },
                                ) {
                                    filteredHeroes.forEach {
                                        DropdownMenuItem(
                                            text = { Text("${it.label}, ${it.quirk}") },
                                            onClick = {
                                                selectedHeroInfo = "${it.label}, ${it.quirk}"
                                                hero = it
                                                expandedHeroesDropdown = false
                                            }
                                        )
                                    }
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
}
