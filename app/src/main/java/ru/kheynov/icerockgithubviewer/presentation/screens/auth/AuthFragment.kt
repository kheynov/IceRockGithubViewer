package ru.kheynov.icerockgithubviewer.presentation.screens.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.icerockgithubviewer.BuildConfig
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.FragmentAuthBinding
import ru.kheynov.icerockgithubviewer.presentation.screens.auth.AuthViewModel.Action
import ru.kheynov.icerockgithubviewer.presentation.screens.auth.AuthViewModel.State.Loading

private const val TAG = "AuthFragment"

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var binding: FragmentAuthBinding

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.apply {
                signInButton.text = if (state is Loading) "" else
                    getString(R.string.sign_in_button_label)
                singInProgressBar.visibility = if (state is Loading) VISIBLE else INVISIBLE
                authTextInputLayout.error =
                    if (state is AuthViewModel.State.InvalidInput) getString(
                        R.string
                            .error_message
                    ) else ""
                signInButton.isEnabled = state !is Loading
            }
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
        // Hiding action bar for auth screen
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.auth_action_bar_label)
            setDisplayHomeAsUpEnabled(false)
        }

        navController = Navigation.findNavController(view)

        binding.authInputText.setText(viewModel.token.value ?: "")
        lifecycleScope.launch {
            viewModel.actions.collect { handleAction(it) }
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.RouteToMain -> {
                navController.navigate(R.id.action_authFragment_to_repositoriesListFragment)
            }

            is Action.ShowError -> {
                if (BuildConfig.DEBUG) Log.i(TAG, action.message.toString())
                val errorDialogFragment = ErrorDialogFragment.create(
                    error = action.error,
                    code = action.HttpCode
                )
                activity?.supportFragmentManager?.let {
                    errorDialogFragment.show(
                        it, getString(R.string.error)
                    )
                }
                binding.signInButton.isEnabled = true
            }
        }
    }
}