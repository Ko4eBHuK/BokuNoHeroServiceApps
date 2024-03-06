package com.preachooda.academyapp.section.home.viewModel

import com.preachooda.assets.util.BaseViewModel
import com.preachooda.academyapp.section.home.domain.HomeScreenIntent
import com.preachooda.academyapp.section.home.domain.HomeScreenState
import com.preachooda.academyapp.section.home.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : BaseViewModel<HomeScreenIntent, HomeScreenState>() {
    private val _homeScreenState: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    override val screenState: StateFlow<HomeScreenState> = _homeScreenState

    override fun processIntent(intent: HomeScreenIntent) {
        when(intent) {
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
}
