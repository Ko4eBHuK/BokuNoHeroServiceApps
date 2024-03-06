package com.preachooda.academyapp.section.application.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.academyapp.section.application.domain.ApplicationScreenIntent
import com.preachooda.academyapp.section.application.domain.ApplicationScreenState
import com.preachooda.academyapp.section.application.repository.ApplicationRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.ActivityStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdmissionViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : BaseViewModel<ApplicationScreenIntent, ApplicationScreenState>() {
    private val _screenState: MutableStateFlow<ApplicationScreenState> = MutableStateFlow(ApplicationScreenState())
    override val screenState: StateFlow<ApplicationScreenState> = _screenState

    override fun processIntent(intent: ApplicationScreenIntent) {
        when (intent) {
            ApplicationScreenIntent.CloseError -> closeError()
            ApplicationScreenIntent.CloseMessage -> closeMessage()
            ApplicationScreenIntent.HandleApplication -> handleApplication()
            is ApplicationScreenIntent.Refresh -> loadApplication(intent.applicationId)
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

    private fun loadApplication(applicationId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getApplication(applicationId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            application = it.data
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

    private fun handleApplication() {
        _screenState.value.application?.let { currentApplication ->
            val newStatus = when (currentApplication.status) {
                ActivityStatus.CREATED -> ActivityStatus.IN_WORK
                ActivityStatus.ASSIGNED -> ActivityStatus.REJECTED
                ActivityStatus.IN_WORK -> ActivityStatus.COMPLETED
                ActivityStatus.EVALUATION -> ActivityStatus.REJECTED
                ActivityStatus.COMPLETED -> ActivityStatus.REJECTED
                ActivityStatus.REJECTED -> ActivityStatus.REJECTED
            }
            val applicationToSend = currentApplication.copy(
                status = newStatus
            )
            viewModelScope.launch(Dispatchers.IO) {
                repository.sendApplication(applicationToSend).collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                application = it.data
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
