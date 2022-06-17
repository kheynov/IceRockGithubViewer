package ru.kheynov.icerockgithubviewer.error_types

sealed interface AuthError {
    object NetworkAuthError : AuthError
    object HttpAuthError : AuthError
}
