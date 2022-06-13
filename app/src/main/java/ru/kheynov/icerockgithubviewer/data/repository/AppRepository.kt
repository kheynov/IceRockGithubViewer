package ru.kheynov.icerockgithubviewer.data.repository

import android.util.Base64
import okio.ByteString.Companion.decodeBase64
import ru.kheynov.icerockgithubviewer.data.api.GithubApi
import ru.kheynov.icerockgithubviewer.data.api.KeyValueStorage
import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.data.entities.RepoDetails
import ru.kheynov.icerockgithubviewer.data.entities.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val githubApi: GithubApi,
    private val keyValueStorage: KeyValueStorage,
) {
    suspend fun getRepositories(): List<Repo> {
        return githubApi.getRepositoriesList(
            authHeader = "Bearer ${keyValueStorage.authToken}",
            userName = keyValueStorage.userName!!
        )
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        return githubApi.getRepositoryDetails(
            ownerName = keyValueStorage.userName!!,
            repoName = repoId,
            authHeader = "Bearer ${keyValueStorage.authToken}",
        )
    }

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String,
    ): String {
        val res = githubApi.getRepositoryReadme(
            ownerName,
            repositoryName,
            branchName,
            authHeader = "Bearer ${keyValueStorage.authToken}"
        )
        return Base64.decode(res.content.toByteArray(), Base64.DEFAULT).decodeToString()
    }

    suspend fun signIn(token: String): UserInfo {
        keyValueStorage.authToken = token
        val res = githubApi.getUserInfo(authHeader = "Bearer ${keyValueStorage.authToken}")
        keyValueStorage.userName = res.login
        return res
    }
}