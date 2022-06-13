package ru.kheynov.icerockgithubviewer.data.repository

import retrofit2.Response
import ru.kheynov.icerockgithubviewer.data.api.GithubApi
import ru.kheynov.icerockgithubviewer.data.api.KeyValueStorage
import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.data.entities.RepoDetails
import ru.kheynov.icerockgithubviewer.data.entities.RepoReadme
import ru.kheynov.icerockgithubviewer.data.entities.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val githubApi: GithubApi,
    private val keyValueStorage: KeyValueStorage,
) {
    suspend fun getRepositories(): Response<List<Repo>> = githubApi.getRepositoriesList(
        authHeader = "Bearer ${keyValueStorage.authToken}",
        userName = keyValueStorage.userName!!
    )


    suspend fun getRepository(repoId: String): Response<RepoDetails> = githubApi
    .getRepositoryDetails(
            ownerName = keyValueStorage.userName!!,
            repoName = repoId,
            authHeader = "Bearer ${keyValueStorage.authToken}",
        )


    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String,
    ): Response<RepoReadme> = githubApi.getRepositoryReadme(
            ownerName,
            repositoryName,
            branchName,
            authHeader = "Bearer ${keyValueStorage.authToken}"
        )


    suspend fun signIn(token: String): Response<UserInfo> {
        keyValueStorage.authToken = token
        val res = githubApi.getUserInfo(
            authHeader = "Bearer ${
                keyValueStorage
                    .authToken
            }"
        )
        keyValueStorage.userName = res.body()?.login
        return res
    }
}