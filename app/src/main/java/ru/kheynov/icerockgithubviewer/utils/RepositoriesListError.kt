package ru.kheynov.icerockgithubviewer.utils

sealed interface RepositoriesListError {
    object NetworkError : RepositoriesListError
    data class Error(val message: String) : RepositoriesListError
}