package com.preachooda.academyapp.section.license.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.academyapp.section.license.domain.LicenseIntent
import com.preachooda.academyapp.section.license.domain.LicenseScreenState
import com.preachooda.academyapp.section.license.repository.LicenseRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.LicenseApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val repository: LicenseRepository
) : BaseViewModel<LicenseIntent, LicenseScreenState>() {
    private val _screenState: MutableStateFlow<LicenseScreenState> =
        MutableStateFlow(LicenseScreenState())
    override val screenState: StateFlow<LicenseScreenState> = _screenState

    override fun processIntent(intent: LicenseIntent) {
        when (intent) {
            LicenseIntent.CloseError -> closeError()
            LicenseIntent.CloseMessage -> closeMessage()
            is LicenseIntent.RegisterLicenseApplication -> registerLicenseApplication(intent)
        }
    }

    private fun closeError() {
        _screenState.value = _screenState.value.copy(
            isError = false
        )
    }

    private fun closeMessage() {
        _screenState.value = _screenState.value.copy(
            isMessage = false
        )
    }

    private fun showError(message: String) {
        _screenState.value = _screenState.value.copy(
            isError = true,
            message = message
        )
    }

    private fun registerLicenseApplication(intent: LicenseIntent.RegisterLicenseApplication) {
        if (intent.printedName.isBlank()) {
            showError("Нужно ввести ФИО")
        } else if (intent.educationDocumentNumber.isBlank()) {
            showError("Нужно ввести номер документа об образовании")
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val licenseApplication = LicenseApplication(
                    printedName = intent.printedName,
                    heroName = intent.heroName,
                    quirk = intent.quirkName,
                    birthDate = intent.birthDate,
                    educationDocumentNumber = intent.educationDocumentNumber
                )
                repository.sendLicenseApplication(licenseApplication).collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isMessage = true,
                                message = "Заявка №${it.data?.id} отправлена"
                            )
                        }
                        Status.ERROR -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isError = true,
                                message = it.message
                            )
                        }
                        Status.LOADING -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = true,
                                message = it.message
                            )
                        }
                    }
                }
            }
        }
    }
}
