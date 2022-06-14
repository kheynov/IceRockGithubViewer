package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.utils.ErrorType

class ErrorDialogFragment(private val error: ErrorType, private val code: Int?) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val message =
                if (error == ErrorType.NetworkError) getString(R.string.network_error)
                else getString(R.string.http_error_code, code)

            builder.setTitle(getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun create(error: ErrorType, code: Int?) = ErrorDialogFragment(error, code)
    }
}