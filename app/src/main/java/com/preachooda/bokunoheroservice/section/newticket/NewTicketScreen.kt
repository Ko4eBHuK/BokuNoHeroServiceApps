package com.preachooda.bokunoheroservice.section.newticket

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.preachooda.bokunoheroservice.BuildConfig
import com.preachooda.bokunoheroservice.R
import com.preachooda.bokunoheroservice.section.newticket.domain.*
import com.preachooda.bokunoheroservice.section.newticket.viewModel.NewTicketViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ConfirmCancelAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.MapView
import com.preachooda.assets.ui.MediaIconButton
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TogglePlate
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryGray
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.bokunoheroservice.utils.createImageFile
import com.preachooda.bokunoheroservice.utils.createVideoFile
import com.preachooda.bokunoheroservice.utils.getImageFileByNameFromPublicDirectory
import com.preachooda.bokunoheroservice.utils.getVideoFileByNameFromPublicDirectory
import java.io.File

@Composable
fun NewTicketScreen(
    navController: NavController,
    viewModel: NewTicketViewModel
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var showLeaveScreenDialog by remember { mutableStateOf(false) }
    BackHandler {
        showLeaveScreenDialog = true
    }

    val screenState = viewModel.screenState.collectAsState().value
    var stateInitialized by rememberSaveable { mutableStateOf(false) }
    if (!stateInitialized) {
        LaunchedEffect(screenState) {
            viewModel.processIntent(Init)
            stateInitialized = true
        }
    }

    // mutable ui values
    var ticketDescription by remember { mutableStateOf("") }
    if (ticketDescription.isBlank() && screenState.ticket.description.isNotBlank()) {
        ticketDescription = screenState.ticket.description
    }

    // Dialogs
    if (screenState.isShowMessage) {
        ConfirmAlertDialog(
            onDismissRequest = {
                viewModel.processIntent(CloseMessageIntent)
                if (screenState.shouldCloseScreen) {
                    navController.popBackStack()
                }
            },
            dialogText = screenState.message
        )
    }
    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(CloseErrorIntent) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(dialogText = screenState.message)
    }
    if (showLeaveScreenDialog) {
        ConfirmCancelAlertDialog(
            dialogText = LEAVE_SCREEN_DIALOG_MESSAGE,
            confirmRequest = {
                viewModel.processIntent(CloseCreation(true, ticketDescription))
                navController.popBackStack()
            },
            cancelRequest = {
                viewModel.processIntent(CloseCreation(false))
                navController.popBackStack()
            },
        )
    }

    Scaffold(
        topBar = { NewTicketTopBar() },
        bottomBar = {
            BottomAppBar {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PrimaryButton(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.processIntent(CreateTicketIntent(ticketDescription))
                        },
                        text = stringResource(id = R.string.new_ticket_screen_btn_create_ticket)
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding()
                )
                .verticalScroll(
                    state = rememberScrollState(),
                ),
        ) {
            // Map
            MapView(
                locationStatus = screenState.locationStatus,
                location = screenState.locationInfo,
                reloadMapCallback = {
                    viewModel.processIntent(GetLocationIntent)
                }
            )

            // Plates section
            NewTicketPlatesSection(
                screenState = screenState,
                viewModel = viewModel,
                focusManager = focusManager
            )

            // Media section
            NewTicketMediaSection(
                context = context,
                viewModel = viewModel,
                screenState = screenState,
                focusManager = focusManager
            )

            // Description
            OutlinedTextField(
                value = ticketDescription,
                onValueChange = {
                    ticketDescription = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.default_padding))
                    .focusRequester(focusRequester)
                    .semantics { contentDescription = "new ticket description" },
                minLines = 1,
                label = {
                    Text(
                        text = NEW_TICKET_DESCRIPTION_FIELD_LABEL
                    )
                },
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTicketTopBar() {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.new_ticket_screen_top_bar_title))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryRed,
            titleContentColor = PrimaryWhite
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NewTicketPlatesSection(
    screenState: NewTicketScreenState,
    viewModel: NewTicketViewModel,
    focusManager: FocusManager
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding)),
        maxItemsInEachRow = 2,
    ) {
        screenState.availableCategories.forEach {
            TogglePlate(
                text = it.value,
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                checked = screenState
                    .selectedCategories.contains(it)
            ) {
                viewModel.processIntent(SelectCategoryIntent(it))
                focusManager.clearFocus()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun NewTicketMediaSection(
    context: Context,
    viewModel: NewTicketViewModel,
    screenState: NewTicketScreenState,
    focusManager: FocusManager
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            VideoMediaOption(
                context = context,
                viewModel = viewModel,
                focusManager = focusManager,
                screenState = screenState
            )
            PhotoMediaOption(
                context = context,
                viewModel = viewModel,
                focusManager = focusManager,
                screenState = screenState
            )
            AudioMediaOption(
                viewModel = viewModel,
                focusManager = focusManager
            )
        }

        if (screenState.photosPaths.isNotEmpty()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_padding))
                ) {
                    var photoPositionToDelete by remember { mutableStateOf(-1) }
                    if (photoPositionToDelete != -1) {
                        ConfirmCancelAlertDialog(
                            dialogText = "Удалить фото?",
                            confirmRequest = {
                                viewModel.processIntent(DeletePhotoIntent(photoPositionToDelete))
                                photoPositionToDelete = -1
                            },
                            cancelRequest = {
                                photoPositionToDelete = -1
                            }
                        )
                    }

                    val haptic = LocalHapticFeedback.current
                    screenState.photosPaths.forEachIndexed { index, photoPath ->
                        getImageFileByNameFromPublicDirectory(photoPath)?.let { photoFile ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(photoFile.absolutePath)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(80.dp)
                                    .padding(dimensionResource(id = R.dimen.default_padding))
                                    .combinedClickable(
                                        onClick = {
                                            try {
                                                val fileUri = FileProvider.getUriForFile(
                                                    context,
                                                    "${BuildConfig.APPLICATION_ID}.provider",
                                                    photoFile
                                                )
                                                val openPhotoIntent = Intent(Intent.ACTION_VIEW)
                                                openPhotoIntent.setDataAndType(fileUri, "image/*")
                                                openPhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                context.startActivity(openPhotoIntent)
                                            } catch (e: Exception) {
                                                viewModel.processIntent(ShowErrorIntent("Ошибка при открытии снимка: ${e.message}"))
                                            }
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            photoPositionToDelete = index
                                        },
                                    ),
                                alignment = Alignment.Center
                            )
                        }
                    }
                }
            }
        }

        if (!screenState.videoPath.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ElevatedButton(
                    onClick = {
                        try {
                            getVideoFileByNameFromPublicDirectory(screenState.videoPath)?.let { videoFile ->
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
                        .fillMaxWidth(.8f)
                        .padding(dimensionResource(id = R.dimen.default_padding)),
                    elevation = ButtonDefaults.elevatedButtonElevation(5.dp)
                ) {
                    Text(text = "Открыть видео")
                }


                var showDeleteVideoDialog by remember { mutableStateOf(false) }
                if (showDeleteVideoDialog) {
                    ConfirmCancelAlertDialog(
                        dialogText = "Удалить видео?",
                        confirmRequest = {
                            viewModel.processIntent(DeleteVideoIntent)
                            showDeleteVideoDialog = false
                        },
                        cancelRequest = {
                            showDeleteVideoDialog = false
                        }
                    )
                }

                IconButton(
                    onClick = {
                        showDeleteVideoDialog = true
                    },
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.default_padding)),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete_bin),
                        contentDescription = "Delete video",
                        tint = PrimaryRed
                    )
                }
            }
        }

        if (!screenState.audioPath.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
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
                        .fillMaxWidth(.8f)
                        .padding(dimensionResource(id = R.dimen.default_padding)),
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

                var showDeleteAudioDialog by remember { mutableStateOf(false) }
                if (showDeleteAudioDialog) {
                    ConfirmCancelAlertDialog(
                        dialogText = "Удалить аудиозапись?",
                        confirmRequest = {
                            viewModel.processIntent(DeleteAudioIntent)
                            showDeleteAudioDialog = false
                        },
                        cancelRequest = {
                            showDeleteAudioDialog = false
                        }
                    )
                }
                IconButton(
                    onClick = {
                        showDeleteAudioDialog = true
                    },
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.default_padding)),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete_bin),
                        contentDescription = "Delete audio",
                        tint = PrimaryRed
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoMediaOption(
    context: Context,
    viewModel: NewTicketViewModel,
    focusManager: FocusManager,
    screenState: NewTicketScreenState
) {
    lateinit var photoFile: File
    lateinit var photoFileUri: Uri

    val photoCameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            viewModel.processIntent(AddPhotoPathIntent(photoFile.name))
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            photoCameraLauncher.launch(photoFileUri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    MediaIconButton(
        onClick = {
            focusManager.clearFocus()
            photoFile = createImageFile(userId = screenState.ticket.userId.toString())
            photoFileUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                photoFile
            )

            val permissionCheckResult = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                photoCameraLauncher.launch(photoFileUri)
            } else {
                // Request a permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        drawableRes = R.drawable.ic_media_photo
    )
}

@Composable
private fun VideoMediaOption(
    context: Context,
    viewModel: NewTicketViewModel,
    focusManager: FocusManager,
    screenState: NewTicketScreenState
) {
    lateinit var videoFile: File
    lateinit var videoFileUri: Uri

    val videoCameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) {
        if (it) {
            viewModel.processIntent(AddVideoPathIntent(videoFile.name))
        } else {
            viewModel.processIntent(ShowErrorIntent("Не удалось записать видео."))
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            videoCameraLauncher.launch(videoFileUri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    MediaIconButton(
        onClick = {
            focusManager.clearFocus()
            videoFile = createVideoFile(userId = screenState.ticket.userId.toString())
            videoFileUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                videoFile
            )

            val permissionCheckResult = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            )
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                videoCameraLauncher.launch(videoFileUri)
            } else {
                // Request a permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        drawableRes = R.drawable.ic_media_video
    )
}

@Composable
private fun AudioMediaOption(
    viewModel: NewTicketViewModel,
    focusManager: FocusManager
) {
    var audioRecording by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    if (audioRecording) {
        DisposableEffect(key1 = audioRecording) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.processIntent(RecordAudioIntent)

            onDispose {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.processIntent(StopAudioRecordingIntent)
            }
        }
    }

    MediaIconButton(
        onClick = {
            focusManager.clearFocus()
            audioRecording = !audioRecording
        },
        drawableRes = R.drawable.ic_media_mic,
        containerColor = if (audioRecording) PrimaryRed else PrimaryGray,
    )
}
