package ru.kheynov.icerockgithubviewer.data.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.data.entities.RepoDetails
import ru.kheynov.icerockgithubviewer.data.entities.RepoReadme
import ru.kheynov.icerockgithubviewer.data.entities.UserInfo

interface GithubApi {

    @GET("user")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String,
    ): UserInfo

    @GET("users/{userName}/repos")
    suspend fun getRepositoriesList(
        @Path("userName") userName: String,
        @Header("Authorization") authHeader: String,
    ): List<Repo>

    @GET("repos/{owner}/{repoName}")
    suspend fun getRepositoryDetails(
        @Path("owner") ownerName: String,
        @Path("repoName") repoName: String,
        @Header("Authorization") authHeader: String,
    ): RepoDetails

    @GET("repos/{owner}/{repoName}/readme")
    suspend fun getRepositoryReadme(
        @Path("owner") ownerName: String,
        @Path("repoName") repoName: String,
        @Query("ref") branchName: String,
        @Header("Authorization") authHeader: String,
    ): RepoReadme
}