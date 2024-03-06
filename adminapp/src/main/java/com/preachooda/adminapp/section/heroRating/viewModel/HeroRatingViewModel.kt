package com.preachooda.adminapp.section.heroRating.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.heroRating.domain.HeroRatingScreenState
import com.preachooda.adminapp.section.heroRating.domain.HeroRatingIntent
import com.preachooda.adminapp.section.heroRating.repository.HeroRatingRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroRatingViewModel @Inject constructor(
    private val repository: HeroRatingRepository
) : BaseViewModel<HeroRatingIntent, HeroRatingScreenState>() {
    private val _screenState: MutableStateFlow<HeroRatingScreenState> =
        MutableStateFlow(HeroRatingScreenState())
    override val screenState: StateFlow<HeroRatingScreenState> = _screenState

    override fun processIntent(intent: HeroRatingIntent) {
        when (intent) {
            HeroRatingIntent.CloseError -> closeError()
            HeroRatingIntent.CloseMessage -> closeMessage()
            is HeroRatingIntent.Refresh -> loadData(intent.heroId)
            is HeroRatingIntent.Submit -> sendHeroRating(intent.newRating)
        }
    }

    private fun loadData(heroId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadHero(heroId).collect { networkCall ->
                when (networkCall.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            isLoading = false,
                            hero = networkCall.data
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

    private fun sendHeroRating(rating: Float) {
        if (_screenState.value.hero != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val heroWithNewRating = _screenState.value.hero!!.copy(rating = rating)
                repository.sendHero(heroWithNewRating).collect { networkCall ->
                    when (networkCall.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                hero = networkCall.data
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
    }
}
