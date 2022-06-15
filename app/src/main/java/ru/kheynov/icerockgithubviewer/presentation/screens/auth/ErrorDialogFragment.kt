package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.DialogErrorBinding
import ru.kheynov.icerockgithubviewer.utils.AuthError

class ErrorDialogFragment(private val error: AuthError, private val code: Int?) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = DialogErrorBinding.inflate(layoutInflater, null, false)

            binding.apply {
                errorDialogDescription.text =
                    if (error == AuthError.NetworkAuthError) getString(R.string.network_error)
                    else getString(R.string.http_error_code, code)

                errorDialogButton.setOnClickListener {
                    dialog?.cancel()
                }
            }
            AlertDialog.Builder(it).setView(binding.root).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun create(error: AuthError, code: Int?) = ErrorDialogFragment(error, code)
    }
}