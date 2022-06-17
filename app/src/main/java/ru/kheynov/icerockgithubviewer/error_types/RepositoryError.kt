package ru.kheynov.icerockgithubviewer.error_types

sealed interface RepositoryError {
    object NetworkError : RepositoryError
    data class Error(val message: String) : RepositoryError
}