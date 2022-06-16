package ru.kheynov.icerockgithubviewer.utils

sealed interface RepositoryError {
    object NetworkError : RepositoryError
    data class Error(val message: String) : RepositoryError
}