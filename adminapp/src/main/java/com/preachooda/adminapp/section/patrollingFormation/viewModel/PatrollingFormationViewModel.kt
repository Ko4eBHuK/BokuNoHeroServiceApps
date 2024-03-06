package com.preachooda.adminapp.section.patrollingFormation.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.patrollingFormation.domain.PatrollingFormationIntent
import com.preachooda.adminapp.section.patrollingFormation.domain.PatrollingFormationScreenState
import com.preachooda.adminapp.section.patrollingFormation.repository.PatrollingFormationRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.Hero
import com.preachooda.domain.model.Patrol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatrollingFormationViewModel @Inject constructor(
    private val repository: PatrollingFormationRepository
) : BaseViewModel<PatrollingFormationIntent, PatrollingFormationScreenState>() {
    private val _screenState: MutableStateFlow<PatrollingFormationScreenState> = MutableStateFlow(PatrollingFormationScreenState())
    override val screenState: StateFlow<PatrollingFormationScreenState> = _screenState

    override fun processIntent(intent: PatrollingFormationIntent) {
        when (intent) {
            PatrollingFormationIntent.CloseError -> closeError()
            PatrollingFormationIntent.CloseMessage -> closeMessage()
            is PatrollingFormationIntent.ConfirmPatrol -> sendPatrolTask(
                intent.district,
                intent.startTime,
                intent.endTime,
                intent.heroes
            )
            PatrollingFormationIntent.Refresh -> loadData()
        }
    }

    private fun loadData() {
        // get heroes
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAvailableHeroes().collect { networkCall ->
                when (networkCall.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            availableHeroes = networkCall.data ?: emptyList()
                        )
                    }
                    Status.ERROR -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isError = true,
                            message = networkCall.message
                        )
                    }
                    Status.LOADING -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = true,
                            message = networkCall.message
                        )
                    }
                }
            }
        }
    }

    private fun sendPatrolTask(
        district: String,
        startTime: String,
        endTime: String,
        heroes: List<Hero>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val patrolTask = Patrol(
                district = district,
                scheduledStart = startTime,
                scheduledEnd = endTime,
                heroes = heroes
            )
            repository.sendPatrol(patrolTask).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            isMessage = true,
                            closeScreen = true,
                            message = "Создан патруль №${patrolTask.id}"
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
