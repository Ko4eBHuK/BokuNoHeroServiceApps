package com.preachooda.adminapp.section.qualificationExamTicket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
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
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.preachooda.adminapp.section.qualificationExamTicket.domain.QualificationExamTicketIntent
import com.preachooda.adminapp.section.qualificationExamTicket.viewModel.QualificationExamTicketViewModel
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
import com.preachooda.assets.ui.theme.SecondaryBlack
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun QualificationExamTicketScreen(
    navController: NavController,
    viewModel: QualificationExamTicketViewModel,
    ticketId: Long
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) viewModel.processIntent(
            QualificationExamTicketIntent.Refresh(ticketId)
        )
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(QualificationExamTicketIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(QualificationExamTicketIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    var selectedOpponent: Hero? by remember { mutableStateOf(null) }
    var selectedInstructor: Hero? by remember { mutableStateOf(null) }
    // dd-MM-yyyy HH:mm:ss
    var selectedStartDate: String by remember { mutableStateOf("") }
    var selectedStartTime: String by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Заявка на экзамен №$ticketId")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryRed,
                    titleContentColor = PrimaryWhite
                )
            )
        },
        bottomBar = {
            if (screenState.ticket?.status == ActivityStatus.CREATED) {
                BottomAppBar {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        PrimaryButton(
                            onClick = {
                                viewModel.processIntent(
                                    QualificationExamTicketIntent.FormTicket(
                                        opponent = selectedOpponent,
                                        instructor = selectedInstructor,
                                        startDateTime = "$selectedStartDate $selectedStartTime:00",
                                    )
                                )
                            },
                            text = "Подтвердить"
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
                    QualificationExamTicketIntent.Refresh(ticketId)
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
            val actionZoneHeight =
                LocalConfiguration.current.screenHeightDp.dp - paddingValues.calculateTopPadding() - paddingValues.calculateBottomPadding()
            LazyColumn {
                if (screenState.ticket != null) {
                    item { // hero
                        Column {
                            TitleText(
                                text = "Экзаменуемый",
                                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
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
                                with(screenState.ticket.hero) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "Имя:",
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = label,
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "Причуда:",
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "$quirk, $skillByQuirk, $quirkType",
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "Показатели:",
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "место в рейтинге $rankingPosition, " +
                                                    "очки рейтинга $rating, " +
                                                    "сила $strength, " +
                                                    "скорость $speed, " +
                                                    "техника $technique, " +
                                                    "интеллект $intelligence, " +
                                                    "работа в команде $cooperation",
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { // opponent
                        Column {
                            TitleText(
                                text = "Противник",
                                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
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
                                if (screenState.ticket.status == ActivityStatus.CREATED) {
                                    var expandedOpponentDropdown by remember { mutableStateOf(false) }
                                    var selectedOpponentInfo by remember { mutableStateOf("") }
                                    ExposedDropdownMenuBox( // Выбор академии 1
                                        expanded = expandedOpponentDropdown,
                                        onExpandedChange = {
                                            expandedOpponentDropdown = !expandedOpponentDropdown
                                        },
                                        modifier = Modifier
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                            .background(PrimaryWhite)
                                    ) {
                                        OutlinedTextField( // Выбор академии
                                            value = selectedOpponentInfo,
                                            onValueChange = {
                                                selectedOpponentInfo = it
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            maxLines = 1,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expandedOpponentDropdown
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

                                            heroInfo.contains(
                                                selectedOpponentInfo,
                                                true
                                            ) && selectedOpponent != hero
                                        }
                                        if (filteredHeroes.isNotEmpty()) {
                                            ExposedDropdownMenu(
                                                expanded = expandedOpponentDropdown,
                                                onDismissRequest = {
                                                    expandedOpponentDropdown = false
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
                                                            selectedOpponent = hero
                                                            selectedOpponentInfo = ""
                                                            expandedOpponentDropdown = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    selectedOpponent?.let { opponent ->
                                        SelectedHeroItem(
                                            hero = opponent,
                                            removeHeroClick = { selectedOpponent = null },
                                        )
                                    }
                                } else {
                                    screenState.ticket.opponent?.let {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "Имя:",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = it.label,
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "Причуда:",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${it.quirk}, ${it.skillByQuirk}, ${it.quirkType}",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "Показатели:",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "место в рейтинге ${it.rankingPosition}, " +
                                                        "очки рейтинга ${it.rating}, " +
                                                        "сила ${it.strength}, " +
                                                        "скорость ${it.speed}, " +
                                                        "техника ${it.technique}, " +
                                                        "интеллект ${it.intelligence}, " +
                                                        "работа в команде $${it.cooperation}",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { // instructor
                        Column {
                            TitleText(
                                text = "Тренер",
                                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
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
                                if (screenState.ticket.status == ActivityStatus.CREATED) {
                                    var expandedInstructorDropdown by remember { mutableStateOf(false) }
                                    var selectedInstructorInfo by remember { mutableStateOf("") }
                                    ExposedDropdownMenuBox( // Выбор академии 1
                                        expanded = expandedInstructorDropdown,
                                        onExpandedChange = {
                                            expandedInstructorDropdown = !expandedInstructorDropdown
                                        },
                                        modifier = Modifier
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                            .background(PrimaryWhite)
                                    ) {
                                        OutlinedTextField( // Выбор академии
                                            value = selectedInstructorInfo,
                                            onValueChange = {
                                                selectedInstructorInfo = it
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            maxLines = 1,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expandedInstructorDropdown
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

                                            heroInfo.contains(
                                                selectedInstructorInfo,
                                                true
                                            ) && selectedInstructor != hero
                                        }
                                        if (filteredHeroes.isNotEmpty()) {
                                            ExposedDropdownMenu(
                                                expanded = expandedInstructorDropdown,
                                                onDismissRequest = {
                                                    expandedInstructorDropdown = false
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
                                                            selectedInstructor = hero
                                                            selectedInstructorInfo = ""
                                                            expandedInstructorDropdown = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    selectedInstructor?.let { instructor ->
                                        SelectedHeroItem(
                                            hero = instructor,
                                            removeHeroClick = { selectedInstructor = null },
                                        )
                                    }
                                } else {
                                    screenState.ticket.instructor?.let {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "Имя:",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = it.label,
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "Причуда:",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${it.quirk}, ${it.skillByQuirk}, ${it.quirkType}",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "Показатели:",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "место в рейтинге ${it.rankingPosition}, " +
                                                        "очки рейтинга ${it.rating}, " +
                                                        "сила ${it.strength}, " +
                                                        "скорость ${it.speed}, " +
                                                        "техника ${it.technique}, " +
                                                        "интеллект ${it.intelligence}, " +
                                                        "работа в команде $${it.cooperation}",
                                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { // date and time
                        if (screenState.ticket.status == ActivityStatus.CREATED) {
                            var showDatePickerDialog by remember { mutableStateOf(false) }
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = System.currentTimeMillis(),
                                initialDisplayMode = DisplayMode.Input,
                                selectableDates = object : SelectableDates {
                                    override fun isSelectableDate(
                                        utcTimeMillis: Long
                                    ) = utcTimeMillis > System.currentTimeMillis()
                                }
                            )
                            if (showDatePickerDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDatePickerDialog = false },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                showDatePickerDialog = false
                                                datePickerState.selectedDateMillis?.let {
                                                    selectedStartDate = SimpleDateFormat("dd-MM-yyyy")
                                                        .format(Date(it))
                                                }
                                            }
                                        ) {
                                            Text(text = "Ок")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDatePickerDialog = false }) {
                                            Text(text = "Закрыть")
                                        }
                                    },
                                    text = {
                                        DatePicker(
                                            state = datePickerState,
                                            headline = {
                                                TitleText(
                                                    text = "Дата экзамена",
                                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                                )
                                            }
                                        )
                                    }
                                )
                            }

                            var showTimePickerDialog by remember { mutableStateOf(false) }
                            var selectedHours by remember { mutableIntStateOf(0) }
                            var selectedMinutes by remember { mutableIntStateOf(0) }
                            val timePickerState = rememberTimePickerState(
                                initialHour = selectedHours,
                                initialMinute = selectedMinutes,
                                is24Hour = true
                            )
                            if (showTimePickerDialog) {
                                AlertDialog(
                                    onDismissRequest = { showTimePickerDialog = false },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                showTimePickerDialog = false
                                                selectedHours = timePickerState.hour
                                                selectedMinutes = timePickerState.minute
                                                val hoursZeroPrefix = if (selectedHours < 10) "0" else ""
                                                val minuteZeroPrefix = if (selectedMinutes < 10) "0" else ""
                                                selectedStartTime = "${hoursZeroPrefix}${selectedHours}:${minuteZeroPrefix}${selectedMinutes}"
                                            }
                                        ) {
                                            Text(text = "Ок")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showTimePickerDialog = false }) {
                                            Text(text = "Закрыть")
                                        }
                                    },
                                    text = {
                                        TimePicker(state = timePickerState)
                                    }
                                )
                            }

                            Column {
                                TitleText(
                                    text = "Дата и время экзамена",
                                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
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
                                    OutlinedTextField( // date
                                        value = selectedStartDate,
                                        onValueChange = {},
                                        modifier = Modifier
                                            .clickable { showDatePickerDialog = true }
                                            .fillMaxWidth()
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                        enabled = false,
                                        minLines = 1,
                                        label = { Text("Дата") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledBorderColor = PrimaryRed,
                                            disabledContainerColor = PrimaryWhite,
                                            disabledTextColor = PrimaryBlack,
                                            disabledLabelColor = PrimaryRed
                                        )
                                    )
                                    OutlinedTextField( // time
                                        value = selectedStartTime,
                                        onValueChange = {},
                                        modifier = Modifier
                                            .clickable { showTimePickerDialog = true }
                                            .fillMaxWidth()
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                        enabled = false,
                                        minLines = 1,
                                        label = { Text("Время") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledBorderColor = PrimaryRed,
                                            disabledContainerColor = PrimaryWhite,
                                            disabledTextColor = PrimaryBlack,
                                            disabledLabelColor = PrimaryRed
                                        )
                                    )
                                }
                            }
                        } else {
                            Column {
                                TitleText(
                                    text = "Время экзамена",
                                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.default_padding))
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
                                    Text(
                                        text = screenState.ticket.startDateTime ?: "Не опредедлено",
                                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .height(actionZoneHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Не удалось загрузить заявку",
                                color = SecondaryBlack
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
