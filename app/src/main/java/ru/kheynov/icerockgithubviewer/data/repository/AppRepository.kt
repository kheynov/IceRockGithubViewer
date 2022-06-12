package ru.kheynov.icerockgithubviewer.data.repository

import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.data.entities.RepoDetails
import ru.kheynov.icerockgithubviewer.data.entities.UserInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor() {

    suspend fun getRepositories(): List<Repo> {
        TODO()
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        TODO()
    }

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String,
    ): String {
        TODO()
    }

    suspend fun signIn(token: String): UserInfo {
        TODO()
    }
}