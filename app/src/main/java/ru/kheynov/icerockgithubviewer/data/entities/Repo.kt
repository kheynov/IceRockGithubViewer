package ru.kheynov.icerockgithubviewer.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val name: String,
    val id: Int,
    val owner: UserInfo,
    val description: String?,
)
