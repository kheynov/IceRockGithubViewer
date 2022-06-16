package ru.kheynov.icerockgithubviewer.presentation.screens.repositories_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.data.repository.AppRepository
import ru.kheynov.icerockgithubviewer.utils.RepositoriesListError
import javax.inject.Inject

private const val TAG = "RepositoriesListViewModel"

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private var fetchRepositoriesJob: Job? = null

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: RepositoriesListError) : State
        object Empty : State
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Error: ", throwable)
        if (throwable.message?.contains("hostname") == true) {
            _state.postValue(State.Error(RepositoriesListError.NetworkError))
        } else {
            _state.postValue(State.Error(RepositoriesListError.Error(throwable.message.toString())))
        }
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
                    } else {
                        _state.postValue(State.Loaded(response.body()!!))
                    }
                } else {
                    _state.postValue(State.Error(RepositoriesListError.Error(response.code()
                        .toString())))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchRepositoriesJob?.cancel()
    }
}