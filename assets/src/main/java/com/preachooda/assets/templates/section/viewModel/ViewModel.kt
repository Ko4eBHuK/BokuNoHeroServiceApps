package com.preachooda.assets.templates.section.viewModel

import com.preachooda.assets.templates.section.domain.ScreenState
import com.preachooda.assets.templates.section.domain.Intent
import com.preachooda.assets.templates.section.repository.Repository
import com.preachooda.assets.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val repository: Repository
) : BaseViewModel<Intent, ScreenState>() {
    private val _screenState: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState())
    override val screenState: StateFlow<ScreenState> = _screenState

    override fun processIntent(intent: Intent) {
        when (intent) {
            Intent.CloseError -> closeError()
            Intent.CloseMessage -> closeMessage()
            // TODO: process other intents
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
