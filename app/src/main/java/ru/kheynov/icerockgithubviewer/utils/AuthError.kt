package ru.kheynov.icerockgithubviewer.utils

sealed interface AuthError {
    object NetworkAuthError : AuthError
    object HttpAuthError : AuthError
}
