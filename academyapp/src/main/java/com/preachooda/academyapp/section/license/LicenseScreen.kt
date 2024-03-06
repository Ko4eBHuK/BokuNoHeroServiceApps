package com.preachooda.academyapp.section.license

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavController
import com.preachooda.academyapp.BuildConfig
import com.preachooda.assets.R
import com.preachooda.academyapp.section.license.domain.LicenseIntent
import com.preachooda.academyapp.section.license.viewModel.LicenseViewModel
import com.preachooda.assets.ui.ConfirmAlertDialog
import com.preachooda.assets.ui.ErrorAlertDialog
import com.preachooda.assets.ui.LoadingDialog
import com.preachooda.assets.ui.PrimaryButton
import com.preachooda.assets.ui.TitleText
import com.preachooda.assets.ui.theme.PrimaryGray
import com.preachooda.assets.ui.theme.PrimaryRed
import com.preachooda.assets.ui.theme.PrimaryWhite
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(
    navController: NavController,
    viewModel: LicenseViewModel
) {
    val screenState = viewModel.screenState.collectAsState().value

    if (screenState.isError) {
        ErrorAlertDialog(
            onDismissRequest = { viewModel.processIntent(LicenseIntent.CloseError) },
            dialogText = screenState.message
        )
    }
    if (screenState.isMessage) {
        ConfirmAlertDialog(
            onDismissRequest = { viewModel.processIntent(LicenseIntent.CloseMessage) },
            dialogText = screenState.message
        )
    }
    if (screenState.isLoading) {
        LoadingDialog(
            dialogText = screenState.message
        )
    }

    var printedName by remember { mutableStateOf("") }
    var heroName by remember { mutableStateOf("") }
    var quirkName by remember { mutableStateOf("") }
    var documentNumber by remember { mutableStateOf("") }
    val millisIn16Years = 16 * 365.25 * 24 * 60 * 60 * 1000
    val now = System.currentTimeMillis()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = (now - millisIn16Years).toLong(),
        initialDisplayMode = DisplayMode.Input,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return (now - utcTimeMillis) > millisIn16Years
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Регистрация заявки на лицензию")
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
                            datePickerState.selectedDateMillis?.let { pickedDate ->
                                val date = Date(pickedDate)
                                val selectedBirthDate = SimpleDateFormat(BuildConfig.DATE_COMMON_FORMAT)
                                    .format(date)
                                viewModel.processIntent(
                                    LicenseIntent.RegisterLicenseApplication(
                                        printedName = printedName,
                                        heroName = heroName,
                                        quirkName = quirkName,
                                        birthDate = selectedBirthDate,
                                        educationDocumentNumber = documentNumber
                                    )
                                )
                            }
                        },
                        text = "Зарегистрировать заявку"
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
                .verticalScroll(rememberScrollState())
        ) {
            TitleText(
                text = "ФИО",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
            )
            OutlinedTextField(
                value = printedName,
                onValueChange = { printedName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.default_padding_twice),
                        end = dimensionResource(id = R.dimen.default_padding_twice)
                    )
                    .semantics { contentDescription = "full name" },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = PrimaryGray,
                    focusedContainerColor = PrimaryWhite,
                    unfocusedContainerColor = PrimaryWhite
                )
            )

            TitleText(
                text = "Геройский псевдоним",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))

            )
            OutlinedTextField(
                value = heroName,
                onValueChange = { heroName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.default_padding_twice),
                        end = dimensionResource(id = R.dimen.default_padding_twice)
                    )
                    .semantics { contentDescription = "hero's nickname" },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = PrimaryGray,
                    focusedContainerColor = PrimaryWhite,
                    unfocusedContainerColor = PrimaryWhite
                )
            )

            TitleText(
                text = "Название причуды",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
            )
            OutlinedTextField(
                value = quirkName,
                onValueChange = { quirkName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.default_padding_twice),
                        end = dimensionResource(id = R.dimen.default_padding_twice)
                    )
                    .semantics { contentDescription = "quirk's name" },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = PrimaryGray,
                    focusedContainerColor = PrimaryWhite,
                    unfocusedContainerColor = PrimaryWhite
                )
            )

            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.default_padding_twice),
                    end = dimensionResource(id = R.dimen.default_padding_twice),
                    bottom = dimensionResource(id = R.dimen.default_padding_twice)
                ),
                title = {},
                headline = {
                    TitleText(
                        text = "Дата рождения",
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
                    )
                }
            )


            TitleText(
                text = "Номер документа об образовании",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding_twice))
            )
            OutlinedTextField(
                value = documentNumber,
                onValueChange = { documentNumber = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.default_padding_twice),
                        end = dimensionResource(id = R.dimen.default_padding_twice),
                        bottom = dimensionResource(id = R.dimen.default_padding_twice)
                    )
                    .semantics { contentDescription = "document num" },
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
