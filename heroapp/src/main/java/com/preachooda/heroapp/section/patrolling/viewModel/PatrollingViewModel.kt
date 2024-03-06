package com.preachooda.heroapp.section.patrolling.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.NetworkCallState
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Patrol
import com.preachooda.heroapp.BuildConfig
import com.preachooda.heroapp.section.patrolling.domain.PatrollingScreenIntent
import com.preachooda.heroapp.section.patrolling.domain.PatrollingScreenState
import com.preachooda.heroapp.section.patrolling.repository.PatrollingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatrollingViewModel @Inject constructor(
    private val repository: PatrollingRepository
) : BaseViewModel<PatrollingScreenIntent, PatrollingScreenState>() {
    private val _screenState: MutableStateFlow<PatrollingScreenState> =
        MutableStateFlow(PatrollingScreenState())
    override val screenState: StateFlow<PatrollingScreenState> = _screenState

    override fun processIntent(intent: PatrollingScreenIntent) {
        when (intent) {
            PatrollingScreenIntent.CloseError -> closeError()
            is PatrollingScreenIntent.ShowError -> showError(intent.message)
            PatrollingScreenIntent.CloseMessage -> closeMessage()
            PatrollingScreenIntent.Refresh -> loadPatrol()
            PatrollingScreenIntent.StartPatrolling -> startPatrolling()
            PatrollingScreenIntent.FinishPatrolling -> finishPatrolling()
        }
    }

    private fun closeError() {
        _screenState.value = _screenState.value.copy(
            isError = false
        )
    }

    private fun showError(message: String) {
        _screenState.value = _screenState.value.copy(
            isError = true,
            message = message
        )
    }

    private fun closeMessage() {
        _screenState.value = _screenState.value.copy(
            isMessage = false
        )
    }

    fun loadPatrol() {
        viewModelScope.launch(Dispatchers.IO) { makeNetworkCallPatrol(repository.getPatrol()) }
    }

    private fun startPatrolling() {
        viewModelScope.launch(Dispatchers.IO) {
            val startedPatrol = _screenState.value.patrol?.copy(
                status = Patrol.Status.STARTED,
                actualStart = SimpleDateFormat(BuildConfig.DATE_COMMON_FORMAT).format(Date())
            )
            if (startedPatrol!= null) {
                makeNetworkCallPatrol(repository.updatePatrol(startedPatrol))
            } else {
                _screenState.value = _screenState.value.copy(
                    isError = true,
                    message = "Данные о патруле не определены"
                )
            }
        }
    }

    private fun finishPatrolling() {
        viewModelScope.launch(Dispatchers.IO) {
            val startedPatrol = _screenState.value.patrol?.copy(
                status = Patrol.Status.COMPLETED,
                actualEnd = SimpleDateFormat(BuildConfig.DATE_COMMON_FORMAT).format(Date())
            )
            if (startedPatrol!= null) {
                makeNetworkCallPatrol(repository.updatePatrol(startedPatrol))
            } else {
                _screenState.value = _screenState.value.copy(
                    isError = true,
                    message = "Данные о патруле не определены"
                )
            }
        }
    }

    private fun <T : NetworkCallState<Patrol?>> makeNetworkCallPatrol(
        networkFlow: Flow<T>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            networkFlow.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            patrol = it.data
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
