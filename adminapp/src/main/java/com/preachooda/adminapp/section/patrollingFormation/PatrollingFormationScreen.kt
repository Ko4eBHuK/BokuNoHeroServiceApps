package com.preachooda.adminapp.section.patrollingFormation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.adminapp.section.patrollingFormation.domain.PatrollingFormationIntent
import com.preachooda.adminapp.section.patrollingFormation.viewModel.PatrollingFormationViewModel
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
fun PatrollingFormationScreen(
    navController: NavController,
    viewModel: PatrollingFormationViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) viewModel.processIntent(PatrollingFormationIntent.Refresh)
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(PatrollingFormationIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = {
                viewModel.processIntent(PatrollingFormationIntent.CloseMessage)
                if (screenState.closeScreen) navController.popBackStack()
            },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    var selectedDistrict by remember { mutableStateOf("") }
    var selectedStartTime by remember { mutableStateOf("") }
    var selectedEndTime by remember { mutableStateOf("") }
    val selectedHeroes = SnapshotStateList<Hero>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Задание на патрулирование")
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
                                PatrollingFormationIntent.ConfirmPatrol(
                                    selectedDistrict,
                                    selectedStartTime,
                                    selectedEndTime,
                                    selectedHeroes
                                )
                            )
                        },
                        text = "Создать"
                    )
                }
            }
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(PatrollingFormationIntent.Refresh)
            }
        )
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn {
                item {
                    TitleText(
                        text = "Район и время",
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
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
                        OutlinedTextField( // district
                            value = selectedDistrict,
                            onValueChange = {
                                selectedDistrict = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.default_padding))
                                .semantics { contentDescription = "area" },
                            minLines = 1,
                            label = { Text("Район") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryRed,
                                unfocusedBorderColor = PrimaryGray,
                                focusedContainerColor = PrimaryWhite,
                                unfocusedContainerColor = PrimaryWhite
                            )
                        )

                        Row( // time interval
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var showTimeDialog by remember { mutableStateOf(false) }
                            var startEndSwitch by remember { mutableStateOf(false) } // false - start, true - end
                            var selectedStartHours by remember { mutableStateOf(0) }
                            var selectedStartMinutes by remember { mutableStateOf(0) }
                            var selectedEndHours by remember { mutableStateOf(0) }
                            var selectedEndMinutes by remember { mutableStateOf(0) }

                            if (showTimeDialog) {
                                val timeState = if (!startEndSwitch) {
                                    rememberTimePickerState(
                                        initialHour = selectedStartHours,
                                        initialMinute = selectedStartMinutes,
                                        is24Hour = true
                                    )
                                } else {
                                    rememberTimePickerState(
                                        initialHour = selectedEndHours,
                                        initialMinute = selectedEndMinutes,
                                        is24Hour = true
                                    )
                                }
                                AlertDialog(
                                    onDismissRequest = { showTimeDialog = false },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                showTimeDialog = false
                                                val hoursZeroPrefix = if (timeState.hour < 10) "0" else ""
                                                val minuteZeroPrefix =
                                                    if (timeState.minute < 10) "0" else ""
                                                val selectedTime =
                                                    "$hoursZeroPrefix${timeState.hour}:$minuteZeroPrefix${timeState.minute}"
                                                if (!startEndSwitch) {
                                                    selectedStartTime = selectedTime
                                                    selectedStartHours = timeState.hour
                                                    selectedStartMinutes = timeState.minute
                                                } else {
                                                    selectedEndTime = selectedTime
                                                    selectedEndHours = timeState.hour
                                                    selectedEndMinutes = timeState.minute
                                                }
                                            }
                                        ) {
                                            Text(text = "Установить")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showTimeDialog = false }) {
                                            Text(text = "Закрыть")
                                        }
                                    },
                                    title = {
                                        TitleText(
                                            text = if (!startEndSwitch) "Выберите время начала"
                                            else "Выберите время окончания"
                                        )
                                    },
                                    text = { TimePicker(state = timeState) }
                                )
                            }

                            OutlinedTextField( // start time
                                value = selectedStartTime,
                                onValueChange = {},
                                modifier = Modifier
                                    .clickable {
                                        showTimeDialog = true
                                        startEndSwitch = false
                                    }
                                    .width(100.dp)
                                    .semantics { contentDescription = "start time" },
                                enabled = false,
                                minLines = 1,
                                label = { Text("Начало") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = PrimaryRed,
                                    disabledContainerColor = PrimaryWhite,
                                    disabledTextColor = PrimaryBlack,
                                    disabledLabelColor = PrimaryRed
                                )
                            )
                            Text(
                                text = "-",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                            )
                            OutlinedTextField( // end time
                                value = selectedEndTime,
                                onValueChange = {},
                                modifier = Modifier
                                    .clickable {
                                        showTimeDialog = true
                                        startEndSwitch = true
                                    }
                                    .width(100.dp)
                                    .semantics { contentDescription = "end time" },
                                enabled = false,
                                minLines = 1,
                                label = { Text("Конец") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = PrimaryRed,
                                    disabledContainerColor = PrimaryWhite,
                                    disabledTextColor = PrimaryBlack,
                                    disabledLabelColor = PrimaryRed
                                )
                            )
                        }
                    }
                }

                item {
                    TitleText(
                        text = "Герои",
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                    )
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.default_padding_twice))
                            .semantics { contentDescription = "hero" },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            contentColor = PrimaryBlack,
                            containerColor = PrimaryWhite
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        )
                    ) {
                        var expandedHeroesDropdown by remember { mutableStateOf(false) }
                        var selectedHeroInfo by remember { mutableStateOf("") }
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
                                    .menuAnchor(),
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

                            val filteredHeroes = screenState.availableHeroes.filter { hero ->
                                val heroInfo = "${hero.label}, ${hero.quirk}," +
                                        " ${hero.quirkType}, ${hero.skillByQuirk}," +
                                        " сила ${hero.strength}, скорость ${hero.speed}," +
                                        " техника ${hero.technique}, интеллект ${hero.intelligence}," +
                                        " работа в команде ${hero.cooperation}"
                                heroInfo.contains(selectedHeroInfo, true) &&
                                        !selectedHeroes.contains(hero)
                            }
                            if (filteredHeroes.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = expandedHeroesDropdown,
                                    onDismissRequest = {
                                        expandedHeroesDropdown = false
                                    },
                                ) {
                                    filteredHeroes.forEach { hero ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${hero.label}, ${hero.quirk}," +
                                                            " ${hero.quirkType}, ${hero.skillByQuirk}," +
                                                            " сила ${hero.strength}, скорость ${hero.speed}," +
                                                            " техника ${hero.technique}, интеллект ${hero.intelligence}," +
                                                            " работа в команде ${hero.cooperation}"
                                                )
                                            },
                                            onClick = {
                                                if (!selectedHeroes.contains(hero)) {
                                                    selectedHeroes.add(hero)
                                                }
                                                selectedHeroInfo = ""
                                                expandedHeroesDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        selectedHeroes.forEach { hero ->
                            SelectedHeroItem(
                                hero = hero,
                                removeHeroClick = { selectedHeroes.remove(hero) }
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
            Text(
                text = "Герой: ${hero.label}",
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "Причуда: ${hero.quirk}, ${hero.quirkType}, ${hero.skillByQuirk}")
            Text(
                text = "Показатели: сила ${hero.strength}, скорость ${hero.speed}," +
                        " техника ${hero.technique}, интеллект ${hero.intelligence}," +
                        " работа в команде ${hero.cooperation}"
            )
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
