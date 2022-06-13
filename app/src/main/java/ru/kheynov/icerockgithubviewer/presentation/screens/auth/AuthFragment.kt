package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.icerockgithubviewer.TestToken
import ru.kheynov.icerockgithubviewer.databinding.FragmentAuthBinding
import ru.kheynov.icerockgithubviewer.presentation.screens.auth.AuthViewModel.Action

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
            when (state) {
                AuthViewModel.State.Loading -> binding.statusTextView.text = "Loading"
                AuthViewModel.State.Idle -> binding.statusTextView.text = "Idle"
                else -> {
                    binding.statusTextView.text = "Error"
                }
            }
        }

        binding.signInButton.setOnClickListener {
            viewModel.onSignInButtonPressed(TestToken.token)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

            is Action.ShowError -> Toast.makeText(
                context,
                "Error: ${action.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}