package ru.kheynov.icerockgithubviewer.presentation.screens.repositories_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kheynov.icerockgithubviewer.BuildConfig
import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.data.repository.AppRepository
import ru.kheynov.icerockgithubviewer.error_types.RepositoryError
import javax.inject.Inject

private const val TAG = "RepositoriesListVM"

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: RepositoryError) : State
        object Empty : State
    }

    private var fetchRepositoriesJob: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (BuildConfig.DEBUG) Log.e(TAG, "Error: ", throwable)

        // If network error
        if (throwable.message?.let { it.contains("hostname") || it.contains("timeout") } == true) {
            _state.postValue(State.Error(RepositoryError.NetworkError))
            return@CoroutineExceptionHandler
        }
        // if error not network-based
        _state.postValue(State.Error(RepositoryError.Error(throwable.message.toString())))
    }

    fun fetchRepositories() {
        fetchRepositoriesJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _state.postValue(State.Loading)
            val response = repository.getRepositories()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    if (response.body().isNullOrEmpty()) {
                        _state.postValue(State.Empty)
                        return@withContext
                    }
                    _state.postValue( // Loading first 10 repositories ordered by update time
                        State.Loaded(response.body()?.take(10) ?: emptyList())
                    )
                    return@withContext
                }
                _state.postValue(
                    State.Error(RepositoryError.Error(response.code().toString()))
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchRepositoriesJob?.cancel()
    }

    fun logOut() = repository.logOut()
}