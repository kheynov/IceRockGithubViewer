package ru.kheynov.icerockgithubviewer.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val login: String,
)
