package ru.kheynov.icerockgithubviewer.data.entities

import android.graphics.Color
import kotlinx.serialization.Serializable
import ru.kheynov.icerockgithubviewer.data.language_colors.LanguageColors

@Serializable
data class Repo(
    val name: String,
    val id: Int,
    val owner: UserInfo,
    val description: String?,
    val language: String?,
) {
    fun getColor(): Int = LanguageColors.colorsOfLanguages[language] ?: Color.WHITE
}
