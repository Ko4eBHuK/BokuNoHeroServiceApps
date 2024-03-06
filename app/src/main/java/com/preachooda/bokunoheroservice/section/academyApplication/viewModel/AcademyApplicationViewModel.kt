package com.preachooda.bokunoheroservice.section.academyApplication.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.bokunoheroservice.section.academyApplication.domain.AcademyApplicationIntent
import com.preachooda.bokunoheroservice.section.academyApplication.domain.AcademyApplicationScreenState
import com.preachooda.bokunoheroservice.section.academyApplication.repository.AcademyApplicationRepository
import com.preachooda.bokunoheroservice.utils.SystemRepository
import com.preachooda.domain.model.AcademyApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademyApplicationViewModel @Inject constructor(
    private val repository: AcademyApplicationRepository,
    private val systemRepository: SystemRepository
) : BaseViewModel<AcademyApplicationIntent, AcademyApplicationScreenState>() {
    private val _screenState: MutableStateFlow<AcademyApplicationScreenState> =
        MutableStateFlow(AcademyApplicationScreenState())
    override val screenState: StateFlow<AcademyApplicationScreenState> = _screenState

    init {
        refresh()
    }

    override fun processIntent(intent: AcademyApplicationIntent) {
        when (intent) {
            AcademyApplicationIntent.CloseError -> closeError()
            AcademyApplicationIntent.CloseMessage -> closeMessage()
            is AcademyApplicationIntent.SendApplication -> sendApplication(intent)
            AcademyApplicationIntent.Refresh -> refresh()
        }
    }

    private fun showError(message: String) {
        _screenState.value = _screenState.value.copy(
            isError = true,
            message = message
        )
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

    private fun refresh() {
        getAcademiesList()
    }

    private fun sendApplication(applicationIntent: AcademyApplicationIntent.SendApplication) {
        if (applicationIntent.printedName.isBlank()) {
            showError("Укажите ФИО")
        } else if (applicationIntent.age <= 0) {
            showError("Укажите возраст")
        } else if (applicationIntent.quirk.isBlank()) {
            showError("Укажите причуду")
        } else if (applicationIntent.educationDocumentNumber.isBlank()) {
            showError("Укажите номер документа об образовании")
        } else if (applicationIntent.firstAcademy == null ||
            applicationIntent.secondAcademy == null ||
            applicationIntent.thirdAcademy == null) {
            showError("Выберите три учебных заведения в порядке приоритета")
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val application = AcademyApplication(
                    userId = systemRepository.getUserId(),
                    printedName = applicationIntent.printedName,
                    age = applicationIntent.age,
                    quirk = applicationIntent.quirk,
                    educationDocumentNumber = applicationIntent.educationDocumentNumber,
                    message = applicationIntent.message,
                    firstAcademy = applicationIntent.firstAcademy,
                    secondAcademy = applicationIntent.secondAcademy,
                    thirdAcademy = applicationIntent.thirdAcademy
                )
                repository.sendApplication(application).collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isMessage = true,
                                isLoading = false,
                                message = "Заявка на поступление отправлена, номер заявки: ${it.data?.id}",
                                closeScreen = true
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

    private fun getAcademiesList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAcademies().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            academies = it.data ?: emptyList()
                        )
                    }
                    Status.ERROR -> {
                        _screenState.value = _screenState.value.copy(
                            isError = true,
                            message = it.message
                        )
                    }
                    Status.LOADING -> {
                        _screenState.value = _screenState.value.copy(
                            message = it.message
                        )
                    }
                }
            }
        }
    }
}
