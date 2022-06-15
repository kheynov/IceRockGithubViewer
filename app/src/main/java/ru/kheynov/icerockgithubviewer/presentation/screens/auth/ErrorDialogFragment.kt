package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.DialogErrorBinding
import ru.kheynov.icerockgithubviewer.utils.ErrorType

class ErrorDialogFragment(private val error: ErrorType, private val code: Int?) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val binding = DialogErrorBinding.inflate(layoutInflater, null, false)

            binding.errorDialogDescription.text =
                if (error == ErrorType.NetworkError) getString(R.string.network_error)
                else getString(R.string.http_error_code, code)

            binding.errorDialogButton.setOnClickListener {
                dialog?.cancel()
            }

            builder.setView(binding.root)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogErrorBinding.inflate(inflater, container, false)

        binding.errorDialogDescription.text = "Network error check your internet connection"

        return binding.root
    }

    companion object {
        fun create(error: ErrorType, code: Int?) = ErrorDialogFragment(error, code)
    }
}