package ru.kheynov.icerockgithubviewer.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class RepoReadme(
    val content: String,
)
