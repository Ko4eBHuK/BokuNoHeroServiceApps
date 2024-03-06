package com.preachooda.bokunoheroservice.section.academyApplication

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.theme.PrimaryBlack
import com.preachooda.assets.ui.theme.PrimaryGray
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import com.preachooda.bokunoheroservice.R
import com.preachooda.bokunoheroservice.section.academyApplication.domain.AcademyApplicationIntent
import com.preachooda.bokunoheroservice.section.academyApplication.viewModel.AcademyApplicationViewModel
import com.preachooda.domain.model.Academy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyApplicationScreen(
    navController: NavController,
    viewModel: AcademyApplicationViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                viewModel.processIntent(AcademyApplicationIntent.Refresh)
            }

            else -> {
                // do nothing
            }
        }
    }

    val screenState = viewModel.screenState.collectAsState().value
    Log.d("AcademyApplicationScreen", "screenState: $screenState")

    // dialogs
    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = {
                viewModel.processIntent(AcademyApplicationIntent.CloseError)
                if (viewModel.screenState.value.closeScreen) navController.popBackStack()
            },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = {
                viewModel.processIntent(AcademyApplicationIntent.CloseMessage)
                if (viewModel.screenState.value.closeScreen) navController.popBackStack()
            },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    // input values
    var printedName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(0) }
    var quirk by remember { mutableStateOf("") }
    var educationDocumentNumber by remember { mutableStateOf("") }
    var applicationMessage by remember { mutableStateOf("") }
    var selectedAcademy1: Academy? by remember { mutableStateOf(null) }
    var selectedAcademy2: Academy? by remember { mutableStateOf(null) }
    var selectedAcademy3: Academy? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            ApplicationTopBar()
        },
        bottomBar = {
            ApplicationBottomBar {
                viewModel.processIntent(
                    AcademyApplicationIntent.SendApplication(
                        printedName,
                        age,
                        quirk,
                        educationDocumentNumber,
                        applicationMessage,
                        selectedAcademy1,
                        selectedAcademy2,
                        selectedAcademy3
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .semantics { contentDescription = "container" }
        ) {
            TitleText(
                text = "Информация о заявителе",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
            )
            ElevatedCard(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice)),
                colors = CardDefaults.cardColors(
                    contentColor = PrimaryBlack,
                    containerColor = PrimaryWhite
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 0.dp
                )
            ) {
                OutlinedTextField( // ФИО
                    value = printedName,
                    onValueChange = {
                        printedName = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.default_padding))
                        .semantics { contentDescription = "full name" },
                    maxLines = 3,
                    label = {
                        Text("ФИО")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = PrimaryGray,
                        focusedContainerColor = PrimaryWhite,
                        unfocusedContainerColor = PrimaryWhite
                    )
                )
                OutlinedTextField( // Возраст
                    value = age.toString(),
                    onValueChange = {
                        if (it.isDigitsOnly() && it.isNotBlank()) {
                            age = it.toInt()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.default_padding))
                        .semantics { contentDescription = "age" },
                    maxLines = 1,
                    label = {
                        Text("Возраст")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = PrimaryGray,
                        focusedContainerColor = PrimaryWhite,
                        unfocusedContainerColor = PrimaryWhite
                    )
                )
                OutlinedTextField( // Причуда
                    value = quirk,
                    onValueChange = {
                        quirk = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.default_padding))
                        .semantics { contentDescription = "quirk" },
                    maxLines = 3,
                    label = {
                        Text("Причуда")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = PrimaryGray,
                        focusedContainerColor = PrimaryWhite,
                        unfocusedContainerColor = PrimaryWhite
                    )
                )
                OutlinedTextField( // Документ об образовании
                    value = educationDocumentNumber,
                    onValueChange = {
                        educationDocumentNumber = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.default_padding))
                        .semantics { contentDescription = "document num" },
                    maxLines = 1,
                    label = {
                        Text("Документ об образовании")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = PrimaryGray,
                        focusedContainerColor = PrimaryWhite,
                        unfocusedContainerColor = PrimaryWhite
                    )
                )
            }

            TitleText(
                text = "Сообщение заявителя",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
            )
            OutlinedTextField( // Сообщение заявителя
                value = applicationMessage,
                onValueChange = {
                    applicationMessage = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice)),
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = PrimaryGray,
                    focusedContainerColor = PrimaryWhite,
                    unfocusedContainerColor = PrimaryWhite
                )
            )

            var expandedAcademies1Dropdown by remember { mutableStateOf(false) }
            var selectedAcademy1Info by remember { mutableStateOf("") }
            TitleText(
                text = "Выбор академии",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
            )
            ExposedDropdownMenuBox( // Выбор академии 1
                expanded = expandedAcademies1Dropdown,
                onExpandedChange = {
                    expandedAcademies1Dropdown = !expandedAcademies1Dropdown
                },
                modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
                    .background(PrimaryWhite)
            ) {
                OutlinedTextField( // Выбор академии
                    value = selectedAcademy1Info,
                    onValueChange = {
                        selectedAcademy1Info = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .semantics { contentDescription = "priority1" },
                    label = {
                        Text("Приоритет 1")
                    },
                    maxLines = 1,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedAcademies1Dropdown
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = PrimaryGray,
                        focusedContainerColor = PrimaryWhite,
                        unfocusedContainerColor = PrimaryWhite
                    )
                )

                val filteredAcademies = screenState.academies.filter {
                    "${it.label}, ${it.address}, ${it.motto}".contains(selectedAcademy1Info, true)
                }
                if (filteredAcademies.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedAcademies1Dropdown,
                        onDismissRequest = { expandedAcademies1Dropdown = false },
                    ) {
                        filteredAcademies.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.label}, ${it.address}, ${it.motto}") },
                                onClick = {
                                    selectedAcademy1Info = "${it.label}, ${it.address}, ${it.motto}"
                                    selectedAcademy1 = it
                                    expandedAcademies1Dropdown = false
                                }
                            )
                        }
                    }
                }
            }

            var expandedAcademies2Dropdown by remember { mutableStateOf(false) }
            var selectedAcademy2Info by remember { mutableStateOf("") }
            ExposedDropdownMenuBox( // Выбор академии 2
                expanded = expandedAcademies2Dropdown,
                onExpandedChange = {
                    expandedAcademies2Dropdown = !expandedAcademies2Dropdown
                },
                modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
                    .background(PrimaryWhite)
            ) {
                OutlinedTextField( // Выбор академии
                    value = selectedAcademy2Info,
                    onValueChange = {
                        selectedAcademy2Info = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .semantics { contentDescription = "priority2" },
                    label = {
                        Text("Приоритет 2")
                    },
                    maxLines = 1,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedAcademies2Dropdown
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = PrimaryGray,
                        focusedContainerColor = PrimaryWhite,
                        unfocusedContainerColor = PrimaryWhite
                    )
                )

                val filteredAcademies = screenState.academies.filter {
                    "${it.label}, ${it.address}, ${it.motto}".contains(selectedAcademy2Info, true)
                }
                if (filteredAcademies.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedAcademies2Dropdown,
                        onDismissRequest = { expandedAcademies2Dropdown = false },
                    ) {
                        filteredAcademies.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.label}, ${it.address}, ${it.motto}") },
                                onClick = {
                                    selectedAcademy2Info = "${it.label}, ${it.address}, ${it.motto}"
                                    selectedAcademy2 = it
                                    expandedAcademies2Dropdown = false
                                }
                            )
                        }
                    }
                }
            }

            var expandedAcademies3Dropdown by remember { mutableStateOf(false) }
            var selectedAcademy3Info by remember { mutableStateOf("") }
            ExposedDropdownMenuBox( // Выбор академии 3
                expanded = expandedAcademies3Dropdown,
                onExpandedChange = {
                    expandedAcademies3Dropdown = !expandedAcademies3Dropdown
                },
                modifier = Modifier.padding(dimensionResource(id = com.preachooda.assets.R.dimen.default_padding_twice))
                    .background(PrimaryWhite)
            ) {
                OutlinedTextField( // Выбор академии
                    value = selectedAcademy3Info,
                    onValueChange = {
                        selectedAcademy3Info = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .semantics { contentDescription = "priority3" },
                    label = {
                        Text("Приоритет 3")
                    },
                    maxLines = 1,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedAcademies3Dropdown
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = PrimaryGray,
                        focusedContainerColor = PrimaryWhite,
                        unfocusedContainerColor = PrimaryWhite
                    )
                )

                val filteredAcademies = screenState.academies.filter {
                    "${it.label}, ${it.address}, ${it.motto}".contains(selectedAcademy3Info, true)
                }
                if (filteredAcademies.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expandedAcademies3Dropdown,
                        onDismissRequest = { expandedAcademies3Dropdown = false },
                    ) {
                        filteredAcademies.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.label}, ${it.address}, ${it.motto}") },
                                onClick = {
                                    selectedAcademy3Info = "${it.label}, ${it.address}, ${it.motto}"
                                    selectedAcademy3 = it
                                    expandedAcademies3Dropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationTopBar() {
    TopAppBar(
        title = {
            Text("Заявка на поступление в академию")
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryRed,
            titleContentColor = PrimaryWhite
        )
    )
}

@Composable
private fun ApplicationBottomBar(
    sendApplicationClick: () -> Unit,
) {
    BottomAppBar {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                onClick = sendApplicationClick,
                text = "Подать заявление"
            )
        }
    }
}
