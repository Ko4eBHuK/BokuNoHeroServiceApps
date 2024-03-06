package com.preachooda.academyapp.section.login.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.academyapp.section.login.domain.AuthenticateIntent
import com.preachooda.academyapp.section.login.domain.CloseErrorIntent
import com.preachooda.academyapp.section.login.domain.CloseMessageIntent
import com.preachooda.academyapp.section.login.domain.LoginScreenIntent
import com.preachooda.academyapp.section.login.domain.LoginScreenState
import com.preachooda.academyapp.section.login.repository.LoginRepository
import com.preachooda.assets.util.Status
import com.preachooda.assets.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : BaseViewModel<LoginScreenIntent, LoginScreenState>() {

    private val _screenState = MutableStateFlow(LoginScreenState())

    override val screenState: StateFlow<LoginScreenState>
        get() = _screenState

    override fun processIntent(intent: LoginScreenIntent) {
        when (intent) {
            CloseErrorIntent -> {
                _screenState.value = _screenState.value.copy(
                    isError = false
                )
            }
            CloseMessageIntent -> {
                _screenState.value = _screenState.value.copy(
                    isError = true
                )
            }
            is AuthenticateIntent -> {
                authenticate(intent.username, intent.password)
            }
        }
    }

    fun defineLoginStatus() = repository.getLoginStatus()

    private fun authenticate(username: String, password: String) {
        if (username.isNotBlank() && password.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.authenticate(username, password).collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                authComplete = true,
                                isLoading = false
                            )
                        }
                        Status.ERROR -> {
                            _screenState.value = _screenState.value.copy(
                                isError = true,
                                isLoading = false,
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
                message = "Пустые значения для логина или пароля недопустимы."
            )
        }
    }
}
