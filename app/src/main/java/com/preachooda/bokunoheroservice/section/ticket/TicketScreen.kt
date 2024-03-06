package com.preachooda.bokunoheroservice.section.ticket

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.preachooda.bokunoheroservice.BuildConfig
import com.preachooda.bokunoheroservice.R
import com.preachooda.assets.util.LocationSimple
import com.preachooda.domain.model.Rate
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Ticket
import com.preachooda.bokunoheroservice.section.newticket.NEW_TICKET_DESCRIPTION_FIELD_PLACEHOLDER
import com.preachooda.bokunoheroservice.section.ticket.domain.CloseErrorIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.CloseMessageIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.DeleteTicketIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.PlayAudioIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.RefreshTicketIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.SaveTicketIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.ShowErrorIntent
import com.preachooda.bokunoheroservice.section.ticket.domain.StopPlayingAudioIntent
import com.preachooda.bokunoheroservice.section.ticket.viewModel.TicketViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ConfirmCancelAlertDialog
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
import com.preachooda.bokunoheroservice.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getVideoFileByNameFromPublicDirectory
import com.preachooda.domain.model.ActivityStatus

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun TicketScreen(
    navController: NavController,
    viewModel: TicketViewModel,
    ticketId: Long
) {
    val screenState = viewModel.screenState.collectAsState().value
    val context = LocalContext.current

    // values of ticket that can be changed  TODO - on first time previous values are shown
    var ticketDescription by mutableStateOf("")
    ticketDescription = screenState.ticket.description
    var ticketComment by mutableStateOf("")
    ticketComment = screenState.ticket.comment
    var heroRates by mutableStateOf(mutableMapOf<Long, Rate>())
    heroRates = screenState.ticket.heroRates.toMutableMap() // TODO - this line may cause empty map
    var ticketRate by mutableStateOf(Rate.NOT_RATED)
    ticketRate = screenState.ticket.rate

    // load ticket by id from navigation
    LaunchedEffect(ticketId) {
        viewModel.initState(ticketId)
    }

    var showDeleteTicketDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TicketScreenTopBar(
                ticketId = ticketId,
                ticket = screenState.ticket,
                deleteTicketCallback = {
                    showDeleteTicketDialog = true
                }
            )
        },
        bottomBar = {
            if (screenState.ticket.status == ActivityStatus.CREATED ||
                screenState.ticket.status == ActivityStatus.EVALUATION
            ) {
                TicketScreenBottomBar(
                    saveTicketCallback = {
                        viewModel.processIntent(
                            SaveTicketIntent(
                                description = ticketDescription,
                                comment = ticketComment,
                                heroRates = heroRates,
                                ticketRate = ticketRate
                            )
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        if (screenState.isShowMessage) {
            ConfirmAlertDialog(
                onDismissRequest = {
                    viewModel.processIntent(CloseMessageIntent)
                    if (screenState.closeScreen) {
                        navController.popBackStack()
                    }
                },
                dialogText = screenState.message
            )
        }
        if (screenState.isError) {
            ErrorAlertDialog(
                onDismissRequest = {
                    viewModel.processIntent(CloseErrorIntent)
                    if (screenState.closeScreen) {
                        navController.popBackStack()
                    }
                },
                dialogText = screenState.message
            )
        }
        if (screenState.isLoading) {
            LoadingDialog(dialogText = screenState.message)
        }
        if (showDeleteTicketDialog) {
            ConfirmCancelAlertDialog(
                dialogText = stringResource(id = R.string.ticket_screen_delete_dialog_text),
                confirmRequest = {
                    showDeleteTicketDialog = false
                    viewModel.processIntent(DeleteTicketIntent)
                },
                cancelRequest = {
                    showDeleteTicketDialog = false
                },
            )
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.isLoading,
            onRefresh = {
                viewModel.processIntent(RefreshTicketIntent)
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
                                                                val fileUri = FileProvider.getUriForFile(
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
                                                                context.startActivity(openPhotoIntent)
                                                            } catch (e: Exception) {
                                                                viewModel.processIntent(ShowErrorIntent("Ошибка при открытии снимка: ${e.message}"))
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
                                                openVideoIntent.setDataAndType(fileUri, "video/*")
                                                openVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                context.startActivity(openVideoIntent)
                                            }
                                        } catch (e: Exception) {
                                            viewModel.processIntent(
                                                ShowErrorIntent("Не удалось включить видео: ${e.message}")
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
                                        viewModel.processIntent(StopPlayingAudioIntent)
                                    }
                                }

                                ElevatedButton(
                                    onClick = {
                                        viewModel.processIntent(PlayAudioIntent)
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
                        if (screenState.ticket.status == ActivityStatus.CREATED) {
                            OutlinedTextField(
                                value = ticketDescription,
                                onValueChange = {
                                    ticketDescription = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.default_padding)),
                                minLines = 1,
                                placeholder = {
                                    Text(NEW_TICKET_DESCRIPTION_FIELD_PLACEHOLDER)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    unfocusedBorderColor = PrimaryGray,
                                    focusedContainerColor = PrimaryWhite,
                                    unfocusedContainerColor = PrimaryWhite
                                )
                            )
                        } else {
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
                }

                if (screenState.ticket.heroes.isNotEmpty()) { // heroes info
                    item(
                        key = screenState.ticket.heroes
                    ) {
                        Column {
                            TitleText(
                                text = "Герои",
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                            )
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
                                    if (screenState.ticket.status == ActivityStatus.EVALUATION ||
                                        screenState.ticket.status == ActivityStatus.COMPLETED
                                    ) {
                                        RateStarsRow(
                                            rate = heroRates[hero.id]
                                                ?: Rate.NOT_RATED,
                                            enabled = screenState.ticket.status == ActivityStatus.EVALUATION,
                                            rateCallback = {
                                                heroRates[hero.id] = it
                                            },
                                            itemKey = hero.label
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (screenState.ticket.status == ActivityStatus.EVALUATION ||
                    screenState.ticket.status == ActivityStatus.COMPLETED
                ) {
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
                                enabled = screenState.ticket.status == ActivityStatus.EVALUATION,
                                rateCallback = {
                                    ticketRate = it
                                }
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
                        if (screenState.ticket.status == ActivityStatus.EVALUATION) {
                            OutlinedTextField(
                                value = ticketComment,
                                onValueChange = {
                                    ticketComment = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.default_padding))
                                    .semantics { contentDescription = "ticket rate comment" },
                                enabled = screenState.ticket.status == ActivityStatus.EVALUATION,
                                minLines = 1,
                                placeholder = {
                                    Text("Ввести...")
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryRed,
                                    unfocusedBorderColor = PrimaryGray,
                                    focusedContainerColor = PrimaryWhite,
                                    unfocusedContainerColor = PrimaryWhite
                                )
                            )
                        } else {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreenTopBar(
    ticketId: Long,
    ticket: Ticket,
    deleteTicketCallback: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Заявка № $ticketId"
            )
        },
        actions = {
            if (ticket.status == ActivityStatus.CREATED) {
                IconButton(onClick = deleteTicketCallback) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete_bin),
                        contentDescription = "Delete ticket",
                        tint = PrimaryWhite
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryRed,
            titleContentColor = PrimaryWhite
        )
    )
}

@Composable
fun TicketScreenBottomBar(
    saveTicketCallback: () -> Unit
) {
    BottomAppBar {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                onClick = saveTicketCallback,
                text = "Сохранить"
            )
        }
    }
}
