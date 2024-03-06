package com.preachooda.bokunoheroservice.section.splash.viewModel

import com.preachooda.bokunoheroservice.navigation.Screens
import com.preachooda.bokunoheroservice.section.splash.domain.SplashScreenIntent
import com.preachooda.bokunoheroservice.section.splash.domain.SplashScreenState
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.bokunoheroservice.utils.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val systemRepository: SystemRepository
) : BaseViewModel<SplashScreenIntent, SplashScreenState>() {

    private val _screenState = MutableStateFlow(SplashScreenState())
    override val screenState: StateFlow<SplashScreenState>
        get() = _screenState

    init {
        if ((systemRepository.getUserToken() ?: "").isNotBlank()) {
            _screenState.value = SplashScreenState(
                nextScreen = Screens.Home
            )
        } else {
            _screenState.value = SplashScreenState(
                nextScreen = Screens.Login
            )
        }
    }

    override fun processIntent(intent: SplashScreenIntent) {
        // Currently unused
    }
}
