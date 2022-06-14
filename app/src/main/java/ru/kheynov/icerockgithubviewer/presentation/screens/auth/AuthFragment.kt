package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.FragmentAuthBinding
import ru.kheynov.icerockgithubviewer.presentation.screens.auth.AuthViewModel.Action
import ru.kheynov.icerockgithubviewer.presentation.screens.auth.AuthViewModel.State.Loading
import ru.kheynov.icerockgithubviewer.utils.ErrorType

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.signInButton.text = if (state is Loading) "" else
                getString(R.string.sign_in_button_label)
            binding.singInProgressBar.visibility = if (state is Loading) VISIBLE else INVISIBLE
            binding.authTextInputLayout.error =
                if (state is AuthViewModel.State.InvalidInput) getString(
                    R.string
                        .error_message
                ) else ""

        }

        binding.signInButton.setOnClickListener {
            binding.authTextInputLayout.clearFocus()
            viewModel.enterToken(binding.authInputText.text.toString())
            viewModel.onSignInButtonPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.authInputText.setText(viewModel.token.value ?: "")
        lifecycleScope.launch {
            viewModel.actions.collect { handleAction(it) }
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.RouteToMain -> Toast.makeText(
                context, "Routing to main !",
                Toast.LENGTH_SHORT
            ).show()

            is Action.ShowError -> {
                Log.i("AuthFragment", action.message.toString())
                if (action.error == ErrorType.NetworkError) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}