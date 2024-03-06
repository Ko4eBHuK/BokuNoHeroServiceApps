package com.preachooda.academyapp.section.applications.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.academyapp.section.applications.domain.ApplicationsIntent
import com.preachooda.academyapp.section.applications.domain.ApplicationsScreenState
import com.preachooda.academyapp.section.applications.repository.ApplicationsRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val repository: ApplicationsRepository
) : BaseViewModel<ApplicationsIntent, ApplicationsScreenState>() {
    private val _screenState: MutableStateFlow<ApplicationsScreenState> = MutableStateFlow(ApplicationsScreenState())
    override val screenState: StateFlow<ApplicationsScreenState> = _screenState

    override fun processIntent(intent: ApplicationsIntent) {
        when (intent) {
            ApplicationsIntent.CloseError -> closeError()
            ApplicationsIntent.CloseMessage -> closeMessage()
            ApplicationsIntent.Refresh -> loadApplications()
        }
    }

    private fun loadApplications() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getApplications().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            applications = it.data ?: emptyList()
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
}
