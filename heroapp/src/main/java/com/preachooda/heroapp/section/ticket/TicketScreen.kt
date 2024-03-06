package com.preachooda.heroapp.section.ticket

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.preachooda.heroapp.BuildConfig
import com.preachooda.assets.util.LocationSimple
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Ticket
import com.preachooda.heroapp.section.ticket.domain.CloseErrorIntent
import com.preachooda.heroapp.section.ticket.domain.CloseMessageIntent
import com.preachooda.heroapp.section.ticket.domain.PlayAudioIntent
import com.preachooda.heroapp.section.ticket.domain.RefreshTicketIntent
import com.preachooda.heroapp.section.ticket.domain.SaveTicketIntent
import com.preachooda.heroapp.section.ticket.domain.ShowErrorIntent
import com.preachooda.heroapp.section.ticket.domain.StopPlayingAudioIntent
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.ExpandableTextPlate
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.MapView
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.fontDimensionResource
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.assets.ui.theme.SecondaryBlack
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.heroapp.section.ticket.viewModel.TicketViewModel
import com.preachooda.heroapp.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.heroapp.utils.getVideoFileByNameFromPublicDirectory

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun TicketScreen(
    navController: NavController,
    viewModel: TicketViewModel
) {
    val screenState = viewModel.screenState.collectAsState().value
    val context = LocalContext.current

    // load ticket by id from navigation
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.processIntent(RefreshTicketIntent)
            }

            else -> {
                // do nothing
            }
        }
    }

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

    if (screenState.ticket != null) {
        Scaffold(
            topBar = {
                TicketScreenTopBar(
                    ticket = screenState.ticket
                )
            },
            bottomBar = {
                TicketScreenBottomBar(
                    ticketStatus = screenState.ticket.status,
                    saveTicketCallback = { viewModel.processIntent(SaveTicketIntent) }
                )
            }
        ) { paddingValues ->
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
                    ) {
                        Column {
                            TitleText(
                                text = "Категории",
                                modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
                            )
                            ExpandableTextPlate(
                                text = screenState.ticket.categories.joinToString(
                                    separator = ", "
                                ) { it.value },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
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
                                modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
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
                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding)),
                                            horizontalArrangement = Arrangement.spacedBy(
                                                dimensionResource(
                                                    id = com.preachooda.assets.R.dimen.default_padding
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
                                                                        ShowErrorIntent(
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
                                            .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice)),
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
                                            .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice)),
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
                                modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
                            )
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
                                Text(
                                    text = screenState.ticket.description.ifBlank { "Отсутствует" },
                                    modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
                                )
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
                                    modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
                                )
                                screenState.ticket.heroes.forEach { hero ->
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
                                        Text(
                                            text = "${hero.label}\n" +
                                                    "Место в рейтинге: ${hero.rankingPosition}\n" +
                                                    "Владение причудой: ${hero.skillByQuirk}",
                                            modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice)),
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
                viewModel.processIntent(RefreshTicketIntent)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Сейчас нет активной заявки на оказание помощи.",
                            modifier = Modifier.fillMaxWidth(.8f),
                            fontSize = fontDimensionResource(id = com.preachooda.assets.R.dimen.default_title_text_size),
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
fun TicketScreenTopBar(
    ticket: Ticket
) {
    TopAppBar(
        title = {
            Text(
                text = "Заявка № ${ticket.id}"
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryRed,
            titleContentColor = PrimaryWhite
        )
    )
}

@Composable
fun TicketScreenBottomBar(
    ticketStatus: ActivityStatus,
    saveTicketCallback: () -> Unit
) {
    if (ticketStatus == ActivityStatus.ASSIGNED || ticketStatus == ActivityStatus.IN_WORK) {
        BottomAppBar {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    onClick = saveTicketCallback,
                    text = when (ticketStatus) {
                        ActivityStatus.ASSIGNED -> "Начать"
                        ActivityStatus.IN_WORK -> "Завершить"
                        else -> "Дейстивие"
                    }
                )
            }
        }
    }
}
