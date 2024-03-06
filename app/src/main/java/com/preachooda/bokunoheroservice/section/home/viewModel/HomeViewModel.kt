package com.preachooda.bokunoheroservice.section.home.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.bokunoheroservice.section.home.domain.Instruction
import com.preachooda.assets.util.Status.*
import com.preachooda.bokunoheroservice.section.home.domain.HomeScreenIntent
import com.preachooda.bokunoheroservice.section.home.domain.HomeScreenState
import com.preachooda.bokunoheroservice.section.home.repository.HomeRepository
import com.preachooda.assets.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : BaseViewModel<HomeScreenIntent, HomeScreenState>() {
    private val _homeScreenState: MutableStateFlow<HomeScreenState> = MutableStateFlow(
        HomeScreenState(
            isLoading = true
        )
    )
    override val screenState: StateFlow<HomeScreenState> = _homeScreenState

    init {
        // TODO - load data for home screen
        val instructions = (0..7).map {
            Instruction(
                id = it,
                label = "Памятка №$it"
            )
        }

        _homeScreenState.value = _homeScreenState.value.copy(
            isLoading = false,
            instructionsList = instructions
        )
    }

    override fun processIntent(intent: HomeScreenIntent) {
        when(intent) {
            is HomeScreenIntent.Sos -> processSosIntent()
            HomeScreenIntent.Logout -> logout()
            HomeScreenIntent.CloseErrorDialog -> closeError()
            HomeScreenIntent.CloseMessageDialog -> closeMessage()
        }
    }

    private fun closeError() {
        _homeScreenState.value = _homeScreenState.value.copy(
            isError = false
        )
    }

    private fun closeMessage() {
        _homeScreenState.value = _homeScreenState.value.copy(
            isShowMessage = false
        )
    }

    private fun logout() {
        repository.logout()
    }

    private fun processSosIntent() {
        viewModelScope.launch(Dispatchers.IO) {
            _homeScreenState.value = _homeScreenState.value.copy(
                isLoading = true,
                message = "Отправка SOS заяки"
            )
            repository.sendSos().collect {
                when(it.status) {
                    SUCCESS -> {
                        _homeScreenState.value = _homeScreenState.value.copy(
                            isLoading = false,
                            isShowMessage = true,
                            message = it.data ?: "Заявка отправлена."
                        )
                    }
                    ERROR -> {
                        _homeScreenState.value = _homeScreenState.value.copy(
                            isLoading = false,
                            isError = true,
                            message = it.data ?: "Не удалось отправить срочную заявку."
                        )
                    }
                    LOADING -> {
                        _homeScreenState.value = _homeScreenState.value.copy(
                            isLoading = true,
                            message = "Отправка SOS заяки"
                        )
                    }
                }
            }
        }
    }
}
