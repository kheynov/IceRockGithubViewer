package ru.kheynov.icerockgithubviewer.data.api

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.kheynov.icerockgithubviewer.data.api.KEYS.AUTH_TOKEN
import ru.kheynov.icerockgithubviewer.data.api.KEYS.USERNAME
import javax.inject.Inject
import javax.inject.Singleton

private const val PREF_NAME = "KEY_VALUE_STORAGE"

enum class KEYS {
    AUTH_TOKEN,
    USERNAME,
}

@Singleton
class KeyValueStorage @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var authToken: String? = null
        set(value) {
            if (field != value) {
                field = value
                saveToPreferences(value, AUTH_TOKEN)
            }
        }
        get() = getFromPreferences(AUTH_TOKEN)

    var userName: String? = null
        set(value) {
            if (field != value) {
                field = value
                saveToPreferences(value, USERNAME)
            }
        }
        get() = getFromPreferences(USERNAME)

    private fun saveToPreferences(value: String?, key: KEYS) {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(key.name, value)
        editor.apply()
    }

    private fun getFromPreferences(key: KEYS) = prefs.getString(key.name, "")
}