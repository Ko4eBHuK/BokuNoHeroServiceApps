package com.preachooda.assets.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<in Intent, out State> : ViewModel() {
    abstract val screenState: StateFlow<State>

    abstract fun processIntent(intent: Intent)
}
