package com.preachooda.adminapp.section.heroesRatings.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.heroesRatings.domain.HeroesRatingsScreenState
import com.preachooda.adminapp.section.heroesRatings.domain.HeroesRatingsIntent
import com.preachooda.adminapp.section.heroesRatings.repository.HeroesRatingsRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroesRatingsViewModel @Inject constructor(
    private val repository: HeroesRatingsRepository
) : BaseViewModel<HeroesRatingsIntent, HeroesRatingsScreenState>() {
    private val _screenState: MutableStateFlow<HeroesRatingsScreenState> = MutableStateFlow(HeroesRatingsScreenState())
    override val screenState: StateFlow<HeroesRatingsScreenState> = _screenState

    override fun processIntent(intent: HeroesRatingsIntent) {
        when (intent) {
            HeroesRatingsIntent.CloseError -> closeError()
            HeroesRatingsIntent.CloseMessage -> closeMessage()
            HeroesRatingsIntent.Refresh -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadHeroes().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            heroes = it.data ?: emptyList()
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
