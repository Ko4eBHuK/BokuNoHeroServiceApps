package com.preachooda.academyapp.section.qualificationExam.viewModel

import androidx.lifecycle.viewModelScope
import com.preachooda.academyapp.section.qualificationExam.domain.QualificationExamIntent
import com.preachooda.academyapp.section.qualificationExam.domain.QualificationExamScreenState
import com.preachooda.academyapp.section.qualificationExam.repository.QualificationExamRepository
import com.preachooda.academyapp.utils.SystemRepository
import com.preachooda.assets.util.BaseViewModel
import com.preachooda.assets.util.Status
import com.preachooda.domain.model.QualificationExamApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QualificationExamViewModel @Inject constructor(
    private val repository: QualificationExamRepository,
    private val systemRepository: SystemRepository
) : BaseViewModel<QualificationExamIntent, QualificationExamScreenState>() {
    private val _screenState: MutableStateFlow<QualificationExamScreenState> = MutableStateFlow(QualificationExamScreenState())
    override val screenState: StateFlow<QualificationExamScreenState> = _screenState

    override fun processIntent(intent: QualificationExamIntent) {
        when (intent) {
            QualificationExamIntent.CloseError -> closeError()
            QualificationExamIntent.CloseMessage -> closeMessage()
            QualificationExamIntent.Refresh -> loadHeroes()
            is QualificationExamIntent.SendApplication -> sendApplication(intent)
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

    private fun loadHeroes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHeroes().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (it.data!= null) {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                heroes = it.data!!
                            )
                        } else {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isError = true,
                                message = "Список героев не определён"
                            )
                        }
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

    private fun sendApplication(intent: QualificationExamIntent.SendApplication) {
        if (intent.hero != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val application = QualificationExamApplication(
                    academyId = systemRepository.getUserId(),
                    hero = intent.hero
                )
                repository.sendQualificationExamApplication(application).collect {
                    when (it.status) {
                        Status.SUCCESS -> {
                            _screenState.value = _screenState.value.copy(
                                isLoading = false,
                                isMessage = true,
                                message = "Заявление №${it.data?.id} на экзамен отправлено"
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
                message = "Не выбран герой."
            )
        }
    }
}
