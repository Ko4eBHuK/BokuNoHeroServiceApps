package com.preachooda.adminapp.section.qualificationExamTickets.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.qualificationExamTickets.domain.QualificationExamTicketsIntent
import com.preachooda.adminapp.section.qualificationExamTickets.domain.QualificationExamTicketsScreenState
import com.preachooda.adminapp.section.qualificationExamTickets.repository.QualificationExamTicketsRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QualificationExamTicketsViewModel @Inject constructor(
    private val repository: QualificationExamTicketsRepository
) : BaseViewModel<QualificationExamTicketsIntent, QualificationExamTicketsScreenState>() {
    private val _screenState: MutableStateFlow<QualificationExamTicketsScreenState> = MutableStateFlow(QualificationExamTicketsScreenState())
    override val screenState: StateFlow<QualificationExamTicketsScreenState> = _screenState

    override fun processIntent(intent: QualificationExamTicketsIntent) {
        when (intent) {
            QualificationExamTicketsIntent.CloseError -> closeError()
            QualificationExamTicketsIntent.CloseMessage -> closeMessage()
            QualificationExamTicketsIntent.Refresh -> loadData()
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

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getExamTickets().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            examApplications = it.data ?: emptyList()
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
                            isLoading = false,
                            message = it.message
                        )
                    }
                }
            }
        }
    }
}
