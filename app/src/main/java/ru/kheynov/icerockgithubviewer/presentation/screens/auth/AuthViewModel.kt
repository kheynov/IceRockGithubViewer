package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.kheynov.icerockgithubviewer.BuildConfig
import ru.kheynov.icerockgithubviewer.data.repository.AppRepository
import ru.kheynov.icerockgithubviewer.utils.ErrorType
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token


    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        if (repository.getToken() != null) {
            _token.value = repository.getToken()
        }
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    private var signInJob: Job? = null

    sealed interface State {
        object Idle : State
        object Loading : State
        object InvalidInput : State
    }

    sealed interface Action {
        data class ShowError(
            val error: ErrorType, val message: String? = null,
            val HttpCode: Int? = null
        ) : Action

        object RouteToMain : Action
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            _actions.send(
                Action.ShowError(
                    error = ErrorType.NetworkError,
                    message = throwable.localizedMessage
                )
            )
        }
    }

    fun onSignInButtonPressed() {
        signInJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _state.postValue(State.Loading)
            val response = repository.signIn(_token.value.toString())
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _state.postValue(State.Idle)
                    _actions.send(Action.RouteToMain)
                } else {
                    if (response.code() == 401) {
                        _state.postValue(State.InvalidInput)
                    } else {
                        if (BuildConfig.DEBUG) Log.i(TAG, "Response code: ${response.code()}")
                        _actions.send(
                            Action.ShowError(
                                error = ErrorType.HttpError,
                                HttpCode = response.code()
                            )
                        )
                        _state.postValue(State.Idle)
                    }
                }
            }
        }
    }

    fun enterToken(token: String) {
        _token.value = token
    }

    override fun onCleared() {
        super.onCleared()
        signInJob?.cancel()
    }
}