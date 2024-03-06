package com.preachooda.adminapp.section.helpTicket

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.preachooda.adminapp.BuildConfig
import com.preachooda.assets.R
import com.preachooda.adminapp.section.helpTicket.domain.HelpTicketIntent
import com.preachooda.adminapp.section.helpTicket.viewModel.HelpTicketViewModel
import com.preachooda.adminapp.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.adminapp.utils.getVideoFileByNameFromPublicDirectory
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ConfirmButton
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.ExpandableTextPlate
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.MapView
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.RateStarsRow
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryGray
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.TextGray
import com.preachooda.assets.util.LocationSimple
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Rate
import com.preachooda.domain.model.TicketComplexity
import com.preachooda.domain.model.TicketPriority

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun HelpTicketScreen(
    navController: NavController,
    viewModel: HelpTicketViewModel,
    ticketId: Long
) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) viewModel.processIntent(
            HelpTicketIntent.Refresh(
                ticketId
            )
        )
    }

    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(HelpTicketIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(HelpTicketIntent.CloseMessage) },
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
            viewModel.processIntent(HelpTicketIntent.Refresh(ticketId))
        }
    )

    var selectedComplexity by remember { mutableStateOf(TicketComplexity.VERY_EASY) }
    var selectedPriority by remember { mutableStateOf(TicketPriority.ONE) }

    val selectedHeroes = remember { mutableStateListOf<Hero>() }
    if (screenState.useAutoHeroes) {
        Log.d("HelpTicket Screen", "Using auto heroes")
        Log.d("HelpTicket Screen", "suitableHeroes ${screenState.suitableHeroes}")
        selectedHeroes.clear()
        selectedHeroes.addAll(screenState.suitableHeroes)
        viewModel.processIntent(HelpTicketIntent.CloseAutoHeroes)
    }
    Log.d("HelpTicket Screen", "selectedHeroes ${selectedHeroes.toList()}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Заявка №$ticketId")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryRed,
                    titleContentColor = PrimaryWhite
                )
            )
        },
        bottomBar = {
            screenState.ticket?.let { ticket ->
                if (ticket.status == ActivityStatus.CREATED) {
                    BottomAppBar {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            ConfirmButton(
                                onClick = {
                                    viewModel.processIntent(
                                        HelpTicketIntent.ConfirmTicket(
                                            selectedHeroes,
                                            selectedComplexity,
                                            selectedPriority
                                        )
                                    )
                                },
                                text = "Отправить"
                            )
                            PrimaryButton(
                                onClick = { viewModel.processIntent(HelpTicketIntent.RejectTicket) },
                                text = "Отклонить"
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn { // Tickets
                if (screenState.ticket != null) {
                    item( // Map
                        key = screenState.ticket.latitude + screenState.ticket.longitude
                    ) {
                        MapView(
                            locationStatus = Status.SUCCESS,
                            location = LocationSimple(
                                latitude = screenState.ticket.latitude,
                                longitude = screenState.ticket.longitude
                            )
                        )
                    }

                    item( // categories
                        key = screenState.ticket.categories
                    ) { // Categories info
                        Column {
                            TitleText(
                                text = "Категории",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                            ExpandableTextPlate(
                                text = screenState.ticket.categories.joinToString(
                                    separator = ", "
                                ) { it.value },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                        }
                    }

                    if ( // media
                        screenState.ticket.photosPaths.isNotEmpty() ||
                        !screenState.ticket.videoPath.isNullOrBlank() ||
                        !screenState.ticket.audioPath.isNullOrBlank()
                    ) {
                        item {
                            TitleText(
                                text = "Медиа",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                        }

                        if (screenState.isFilesLoading) {
                            item {
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
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center,
                                        content = { Text(screenState.message) }
                                    )
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 16.dp,
                                                bottom = 16.dp
                                            ),
                                        color = PrimaryRed
                                    )
                                }
                            }
                        } else {
                            if (screenState.ticket.photosPaths.isNotEmpty()) {
                                item( // photos
                                    key = screenState.ticket.photosPaths
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
                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(dimensionResource(id = R.dimen.default_padding)),
                                            horizontalArrangement = Arrangement.spacedBy(
                                                dimensionResource(
                                                    id = R.dimen.default_padding
                                                )
                                            )
                                        ) {
                                            screenState.ticket.photosPaths.forEach { photoPath ->
                                                getImageFileByNameFromPublicDirectory(photoPath)?.let { photoFile ->
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(context)
                                                            .data(photoFile.absolutePath)
                                                            .build(),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .height(80.dp)
                                                            .clickable {
                                                                try {
                                                                    val fileUri =
                                                                        FileProvider.getUriForFile(
                                                                            context,
                                                                            "${BuildConfig.APPLICATION_ID}.provider",
                                                                            photoFile
                                                                        )
                                                                    val openPhotoIntent =
                                                                        Intent(Intent.ACTION_VIEW)
                                                                    openPhotoIntent.setDataAndType(
                                                                        fileUri,
                                                                        "image/*"
                                                                    )
                                                                    openPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                                    context.startActivity(
                                                                        openPhotoIntent
                                                                    )
                                                                } catch (e: Exception) {
                                                                    viewModel.processIntent(
                                                                        HelpTicketIntent.ShowErrorIntent(
                                                                            "Ошибка при открытии снимка: ${e.message}"
                                                                        )
                                                                    )
                                                                }
                                                            },
                                                        alignment = Alignment.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (!screenState.ticket.videoPath.isNullOrBlank()) {
                                item( // video
                                    key = screenState.ticket.videoPath
                                ) {
                                    ElevatedButton(
                                        onClick = {
                                            try {
                                                getVideoFileByNameFromPublicDirectory(
                                                    screenState.ticket.videoPath ?: ""
                                                )?.let { videoFile ->
                                                    val fileUri = FileProvider.getUriForFile(
                                                        context,
                                                        "${BuildConfig.APPLICATION_ID}.provider",
                                                        videoFile
                                                    )
                                                    val openVideoIntent = Intent(Intent.ACTION_VIEW)
                                                    openVideoIntent.setDataAndType(
                                                        fileUri,
                                                        "video/*"
                                                    )
                                                    openVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    context.startActivity(openVideoIntent)
                                                }
                                            } catch (e: Exception) {
                                                viewModel.processIntent(
                                                    HelpTicketIntent.ShowErrorIntent("Не удалось включить видео: ${e.message}")
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                        elevation = ButtonDefaults.elevatedButtonElevation(5.dp)
                                    ) {
                                        Text(text = "Открыть видео")
                                    }
                                }
                            }

                            if (!screenState.ticket.audioPath.isNullOrBlank()) {
                                item( // audio
                                    key = screenState.ticket.audioPath
                                ) {
                                    DisposableEffect(key1 = Unit) {
                                        onDispose {
                                            viewModel.processIntent(HelpTicketIntent.StopPlayingAudioIntent)
                                        }
                                    }

                                    ElevatedButton(
                                        onClick = {
                                            viewModel.processIntent(HelpTicketIntent.PlayAudioIntent)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice)),
                                        elevation = if (screenState.isAudioPlaying) ButtonDefaults.elevatedButtonElevation(
                                            0.dp
                                        )
                                        else ButtonDefaults.elevatedButtonElevation(5.dp)
                                    ) {
                                        Text(
                                            text = if (screenState.isAudioPlaying) "Остановить воспроизведение"
                                            else "Воспроизвести аудиозапись"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item( // description
                        key = screenState.ticket.description
                    ) {
                        Column {
                            TitleText(
                                text = "Описание заявки",
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
                                Text(
                                    text = screenState.ticket.description.ifBlank { "Отсутствует" },
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                )
                            }
                        }
                    }

                    item( // heroes
                        key = screenState.ticket.heroes
                    ) {
                        Column {
                            TitleText(
                                text = "Герои",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
                            if (screenState.ticket.status == ActivityStatus.CREATED) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                        .semantics { contentDescription = "heroes" },
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
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        var expandedHeroesDropdown by remember {
                                            mutableStateOf(
                                                false
                                            )
                                        }
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
                                                    .fillMaxWidth(.8f)
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

                                            val filteredHeroes =
                                                screenState.availableHeroes.filter { hero ->
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

                                        TextButton(
                                            onClick = {
                                                viewModel.processIntent(
                                                    HelpTicketIntent.AutoHeroes(
                                                        complexity = selectedComplexity,
                                                        priority = selectedPriority,
                                                    )
                                                )
                                            },
                                            content = { Text(text = "Авто") }
                                        )
                                    }

                                    selectedHeroes.forEach { hero ->
                                        SelectedHeroItem(
                                            hero = hero,
                                            removeHeroClick = { selectedHeroes.remove(hero) }
                                        )
                                    }
                                }
                            } else {
                                screenState.ticket.heroes.forEach { hero ->
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
                                        if (screenState.ticket.status == ActivityStatus.COMPLETED) {
                                            RateStarsRow(
                                                rate = screenState.ticket.heroRates[hero.id]
                                                    ?: Rate.NOT_RATED,
                                                enabled = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (screenState.ticket.status == ActivityStatus.CREATED) {
                        item { // complexity and priority
                            Column(
                                // complexity
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                TitleText(
                                    text = "Сложность и приоритет",
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                    var complexityExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = complexityExpanded,
                                        onExpandedChange = {
                                            complexityExpanded = !complexityExpanded
                                        },
                                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                    ) {
                                        OutlinedTextField(
                                            value = selectedComplexity.value,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = complexityExpanded
                                                )
                                            },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .semantics { contentDescription = "hard" },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = PrimaryBlack,
                                                unfocusedTextColor = PrimaryBlack,
                                                focusedBorderColor = PrimaryRed,
                                                unfocusedBorderColor = PrimaryRed,
                                                focusedContainerColor = PrimaryWhite,
                                                unfocusedContainerColor = PrimaryWhite
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = complexityExpanded,
                                            onDismissRequest = { complexityExpanded = false }
                                        ) {
                                            TicketComplexity.entries.forEach { complexity ->
                                                DropdownMenuItem(
                                                    text = { Text(text = complexity.value) },
                                                    onClick = {
                                                        selectedComplexity = complexity
                                                        complexityExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    var priorityExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = priorityExpanded,
                                        onExpandedChange = { priorityExpanded = !priorityExpanded },
                                        modifier = Modifier
                                            .padding(dimensionResource(id = R.dimen.default_padding_twice))
                                            .wrapContentSize()
                                    ) {
                                        OutlinedTextField(
                                            value = selectedPriority.value.toString(),
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = priorityExpanded
                                                )
                                            },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .semantics { contentDescription = "priority" },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = PrimaryBlack,
                                                unfocusedTextColor = PrimaryBlack,
                                                focusedBorderColor = PrimaryRed,
                                                unfocusedBorderColor = PrimaryRed,
                                                focusedContainerColor = PrimaryWhite,
                                                unfocusedContainerColor = PrimaryWhite
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = priorityExpanded,
                                            onDismissRequest = { priorityExpanded = false }
                                        ) {
                                            TicketPriority.entries.forEach { priority ->
                                                DropdownMenuItem(
                                                    text = { Text(text = priority.value.toString()) },
                                                    onClick = {
                                                        selectedPriority = priority
                                                        priorityExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (screenState.ticket.status == ActivityStatus.COMPLETED) {
                        item( // ticket rate
                            key = screenState.ticket.rate
                        ) {
                            Column {
                                TitleText(
                                    text = "Оценка заявки",
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                )
                                RateStarsRow(
                                    rate = screenState.ticket.rate,
                                    enabled = false
                                )
                            }
                        }

                        item( // ticket comment
                            key = screenState.ticket.comment
                        ) {
                            TitleText(
                                text = "Комментарий",
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
                                Text(
                                    text = screenState.ticket.comment.ifBlank { "Не был оставлен" },
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Не удалось загрузить информацию о заявке",
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
