package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kheynov.icerockgithubviewer.BuildConfig
import ru.kheynov.icerockgithubviewer.data.repository.AppRepository
import ru.kheynov.icerockgithubviewer.error_types.AuthError
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val tokenValidationPattern = "^[a-z_0-9]+$".toRegex(RegexOption.IGNORE_CASE)

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token


    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state


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
            val error: AuthError, val message: String? = null,
            val HttpCode: Int? = null,
        ) : Action

        object RouteToMain : Action
    }

    init {
        if (repository.isAuthorized) _actions.trySend(Action.RouteToMain)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            _actions.send(
                Action.ShowError(
                    error = AuthError.NetworkAuthError,
                    message = throwable.localizedMessage
                )
            )
        }
    }

    fun onSignInButtonPressed() {
        signInJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _state.postValue(State.Loading)

            val matches = token.value.toString().matches(tokenValidationPattern)
            Log.i(TAG, "Matches: $matches")
            if (!token.value.toString().matches(tokenValidationPattern)) {
                _state.postValue(State.InvalidInput)
                return@launch
            }

            val response = repository.signIn(_token.value.toString())
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _state.postValue(State.Idle)
                    _actions.send(Action.RouteToMain)
                    return@withContext
                }

                if (response.code() == 401) {
                    _state.postValue(State.InvalidInput)
                    return@withContext
                }
                if (BuildConfig.DEBUG) Log.i(TAG, "Response code: ${response.code()}")
                _actions.send(
                    Action.ShowError(
                        error = AuthError.HttpAuthError,
                        HttpCode = response.code()
                    )
                )
                _state.postValue(State.Idle)
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