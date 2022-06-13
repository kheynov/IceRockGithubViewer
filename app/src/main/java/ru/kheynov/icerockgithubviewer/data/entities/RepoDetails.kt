package ru.kheynov.icerockgithubviewer.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetails(
    val name: String,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
    @SerialName("stargazers_count") val starsCount: Int,
    val license: String?,
)
