package ru.kheynov.icerockgithubviewer.utils

sealed interface ErrorType {
    object NetworkError : ErrorType
    object HttpError : ErrorType
}
