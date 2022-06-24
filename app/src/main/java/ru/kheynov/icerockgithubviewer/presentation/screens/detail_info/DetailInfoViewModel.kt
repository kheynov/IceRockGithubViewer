package ru.kheynov.icerockgithubviewer.presentation.screens.detail_info

import android.util.Base64
import android.util.Log
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

    var state = MutableLiveData<State>()
        private set

    var readmeState = MutableLiveData<ReadmeState>()
        private set

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
            state.postValue(State.Error(RepositoryError.NetworkError))
            return@CoroutineExceptionHandler
        }
        state.postValue(State.Error(RepositoryError.Error(throwable.message.toString())))
    }

    private val fetchReadmeExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (BuildConfig.DEBUG) Log.e(TAG, "Error: ", throwable)
        if (throwable.message?.contains("hostname") == true) {
            readmeState.postValue(ReadmeState.Error(RepositoryError.NetworkError))
        } else {
            readmeState.postValue(ReadmeState.Error(RepositoryError.Error(throwable.message.toString())))
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
                state.postValue(State.Loading)
                val response = repository.getRepository(repositoryName)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        state.postValue(State.Loaded(response.body()!!))
                        if (response.body() is RepoDetails) response.body()?.let { repo ->
                            fetchReadme(
                                repositoryName = repo.name,
                                defaultBranch = repo.defaultBranch,
                                owner = repo.owner.login
                            )
                        }
                        return@withContext
                    }
                    state.postValue(
                        State.Error(
                            RepositoryError.Error(response.code().toString())
                        )
                    )
                }
            }
    }

    fun fetchReadme(repositoryName: String, defaultBranch: String, owner: String) {
        fetchRepositoryReadmeJob =
            CoroutineScope(Dispatchers.IO + fetchReadmeExceptionHandler).launch {
                readmeState.postValue(ReadmeState.Loading)
                val response = repository.getRepositoryReadme(
                    ownerName = owner,
                    repositoryName = repositoryName,
                    branchName = defaultBranch,
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        readmeState.postValue(ReadmeState.Loaded(response.body()!!.content))
                        return@withContext
                    }
                    if (response.code() == 404) {
                        readmeState.postValue(ReadmeState.Empty)
                        return@withContext
                    }
                    readmeState.postValue(
                        ReadmeState.Error(
                            RepositoryError.Error(
                                response.code().toString()
                            )
                        )
                    )
                }
            }
    }

    fun logOut() = repository.logOut()

}