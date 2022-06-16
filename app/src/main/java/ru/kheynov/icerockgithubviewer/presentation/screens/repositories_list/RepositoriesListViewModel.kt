package ru.kheynov.icerockgithubviewer.presentation.screens.repositories_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import ru.kheynov.icerockgithubviewer.BuildConfig
import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.data.repository.AppRepository
import ru.kheynov.icerockgithubviewer.utils.RepositoryError
import javax.inject.Inject

private const val TAG = "RepositoriesListVM"

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
        data class Error(val error: RepositoryError) : State
        object Empty : State
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (BuildConfig.DEBUG) Log.e(TAG, "Error: ", throwable)
        if (throwable.message?.contains("hostname") == true) {
            _state.postValue(State.Error(RepositoryError.NetworkError))
        } else {
            _state.postValue(State.Error(RepositoryError.Error(throwable.message.toString())))
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
                    } else {
                        _state.postValue(State.Loaded(response.body()!!))//TODO: .take(10)
                    }
                } else {
                    _state.postValue(State.Error(RepositoryError.Error(response.code()
                        .toString())))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchRepositoriesJob?.cancel()
    }

    fun logOut() = repository.logOut()

}