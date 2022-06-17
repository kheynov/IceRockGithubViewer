package ru.kheynov.icerockgithubviewer.presentation.screens.detail_info

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import ru.kheynov.icerockgithubviewer.BuildConfig
import ru.kheynov.icerockgithubviewer.data.entities.RepoDetails
import ru.kheynov.icerockgithubviewer.data.repository.AppRepository
import ru.kheynov.icerockgithubviewer.error_types.RepositoryError
import javax.inject.Inject

private const val TAG = "DetailInfoVM"

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private var fetchRepositoryDetailsJob: Job? = null
    private var fetchRepositoryReadmeJob: Job? = null

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _readmeState = MutableLiveData<ReadmeState>()
    val readmeState: LiveData<ReadmeState>
        get() = _readmeState

    sealed interface State {
        object Loading : State
        data class Error(val error: RepositoryError) : State

        data class Loaded(
            val githubRepo: RepoDetails,
        ) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: RepositoryError) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState {
            fun markdownToString(): String {
                return String(Base64.decode(markdown, Base64.DEFAULT), charset("UTF-8"))
            }
        }
    }

    private val fetchRepositoryExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (BuildConfig.DEBUG) Log.e(TAG, "Error: ", throwable)
        if (throwable.message?.contains("hostname") == true) {
            _state.postValue(State.Error(RepositoryError.NetworkError))

        } else {
            _state.postValue(State.Error(RepositoryError.Error(throwable.message.toString())))
        }
    }
    private val fetchReadmeExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (BuildConfig.DEBUG) Log.e(TAG, "Error: ", throwable)
        if (throwable.message?.contains("hostname") == true) {
            _readmeState.postValue(ReadmeState.Error(RepositoryError.NetworkError))
        } else {
            _readmeState.postValue(ReadmeState.Error(RepositoryError.Error(throwable.message.toString())))
        }
    }


    override fun onCleared() {
        super.onCleared()
        fetchRepositoryDetailsJob?.cancel()
        fetchRepositoryReadmeJob?.cancel()
    }

    fun fetchRepository(repositoryName: String) {
        fetchRepositoryDetailsJob =
            CoroutineScope(Dispatchers.IO + fetchRepositoryExceptionHandler).launch {
                _state.postValue(State.Loading)
                val response = repository.getRepository(repositoryName)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _state.postValue(State.Loaded(response.body()!!))
                        if (response.body() is RepoDetails) response.body()?.let {
                            fetchReadme(
                                repositoryName = it.name,
                                defaultBranch = it.defaultBranch,
                                owner = it.owner.login
                            )
                        }
                    } else {
                        _state.postValue(
                            State.Error(
                                RepositoryError.Error(response.code().toString())
                            )
                        )
                    }
                }
            }
    }

    fun fetchReadme(repositoryName: String, defaultBranch: String, owner: String) {
        fetchRepositoryReadmeJob =
            CoroutineScope(Dispatchers.IO + fetchReadmeExceptionHandler).launch {
                _readmeState.postValue(ReadmeState.Loading)
                val response = repository.getRepositoryReadme(
                    ownerName = owner,
                    repositoryName = repositoryName,
                    branchName = defaultBranch,
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _readmeState.postValue(ReadmeState.Loaded(response.body()!!.content))
                    } else {
                        if (response.code() == 404) {
                            _readmeState.postValue(ReadmeState.Empty)
                        } else {
                            _readmeState.postValue(ReadmeState.Error(RepositoryError.Error(
                                response.code().toString())))
                        }
                    }
                }
            }
    }

    fun logOut() = repository.logOut()

}