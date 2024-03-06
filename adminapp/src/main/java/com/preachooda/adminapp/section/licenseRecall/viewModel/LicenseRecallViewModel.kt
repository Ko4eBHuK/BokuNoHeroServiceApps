package com.preachooda.adminapp.section.licenseRecall.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.adminapp.section.licenseRecall.domain.LicenseRecallScreenState
import com.preachooda.adminapp.section.licenseRecall.domain.LicenseRecallIntent
import com.preachooda.adminapp.section.licenseRecall.repository.LicenseRecallRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.ActivityStatus
import com.preachooda.domain.model.Hero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseRecallViewModel @Inject constructor(
    private val repository: LicenseRecallRepository
) : BaseViewModel<LicenseRecallIntent, LicenseRecallScreenState>() {
    private val _screenState: MutableStateFlow<LicenseRecallScreenState> = MutableStateFlow(LicenseRecallScreenState())
    override val screenState: StateFlow<LicenseRecallScreenState> = _screenState

    override fun processIntent(intent: LicenseRecallIntent) {
        when (intent) {
            LicenseRecallIntent.CloseError -> closeError()
            LicenseRecallIntent.CloseMessage -> closeMessage()
            LicenseRecallIntent.Refresh -> loadData()
            is LicenseRecallIntent.RecallIntent -> recallLicense(intent.hero)
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) { // get heroes
            repository.loadHeroes().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        _screenState.value = _screenState.value.copy(
                            heroes = it.data ?: emptyList(),
                            isLoading = false
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

    private fun recallLicense(hero: Hero?) {
        if (hero != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.recallHeroLicense(hero).collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isMessage = true,
                                isLoading = false,
                                message = "У героя ${hero.label} отозвана лицензия"
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
        } else {
            _screenState.value = _screenState.value.copy(
                isError = true,
                message = "Должен быть выбран герой"
            )
        }
    }
}
