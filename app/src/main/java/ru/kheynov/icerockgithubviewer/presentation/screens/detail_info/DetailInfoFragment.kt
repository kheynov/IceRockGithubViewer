package ru.kheynov.icerockgithubviewer.presentation.screens.detail_info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import ru.kheynov.icerockgithubviewer.BuildConfig
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.FragmentDetailInfoBinding
import ru.kheynov.icerockgithubviewer.presentation.screens.detail_info.DetailInfoViewModel.ReadmeState
import ru.kheynov.icerockgithubviewer.presentation.screens.detail_info.DetailInfoViewModel.State.Loaded
import ru.kheynov.icerockgithubviewer.presentation.screens.detail_info.DetailInfoViewModel.State.Loading
import ru.kheynov.icerockgithubviewer.error_types.RepositoryError

private const val TAG = "DetailInfo"

@AndroidEntryPoint
class DetailInfoFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var binding: FragmentDetailInfoBinding

    private val viewModel by viewModels<DetailInfoViewModel>()

    private val repoName: String
        get() = requireNotNull(requireArguments().getString(REPOSITORY_NAME_KEY))

    private lateinit var markDownRenderer: Markwon

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailInfoBinding.inflate(
            inflater,
            container,
            false
        )

        if (viewModel.state.value !is Loaded) viewModel.fetchRepository(repoName)

        binding.detailInfoErrorButton.setOnClickListener {
            if (viewModel.state.value is Loaded) {
                (viewModel.state.value as Loaded).githubRepo.let {
                    viewModel.fetchReadme(
                        it.name,
                        it.defaultBranch,
                        it.owner.login
                    )
                }
            } else {
                viewModel.fetchRepository(repoName)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (BuildConfig.DEBUG) Log.i(TAG, state.toString())
            binding.apply {
                repositoryDetailInfoBlock.visibility =
                    if (state is Loaded) View.VISIBLE else View
                        .INVISIBLE
                forksCount.text =
                    if (state is Loaded) state.githubRepo.forksCount.toString() else ""
                starsCount.text =
                    if (state is Loaded) state.githubRepo.starsCount.toString() else ""
                watchersCount.text =
                    if (state is Loaded) state.githubRepo.watchersCount.toString() else ""
                license.text =
                    if (state is Loaded)
                        state.githubRepo.license?.name ?: "No license"
                    else ""
                link.text =
                    if (state is Loaded) state.githubRepo.url.substringAfter("//") else ""

                link.setOnClickListener {
                    if (state is Loaded) openUrl(state.githubRepo.url)
                }

                detailInfoLoadingPb.visibility = if (state is Loading) View.VISIBLE else View
                    .INVISIBLE

                bindErrorScreen(
                    visibility = state is DetailInfoViewModel.State.Error,
                    error = if (state is DetailInfoViewModel.State.Error) state.error else null,
                )

            }
        }

        viewModel.readmeState.observe(viewLifecycleOwner) { state ->
            Log.i(TAG, state.toString())
            binding.apply {
                repositoryDetailReadmeBlock.visibility =
                    if (state !is ReadmeState.Loading) View.VISIBLE else View.INVISIBLE

                if (state is ReadmeState.Empty) {
                    readmeTextView.text = "No README.MD"
                }

                if (state is ReadmeState.Loaded) {
                    markDownRenderer.setMarkdown(readmeTextView,
                        state.markdownToString())
                }
                readmeLoadingPb.visibility = if (state is ReadmeState.Loading) View.VISIBLE else
                    View.INVISIBLE

                bindErrorScreen(
                    visibility = state is ReadmeState.Error,
                    error = if (state is ReadmeState.Error) state.error else null,
                )
            }

        }
        return binding.root
    }

    private fun FragmentDetailInfoBinding.bindErrorScreen(
        // binding error view
        visibility: Boolean = false,
        error: RepositoryError?,
    ) {
        detailInfoErrorButton.apply {
            setOnClickListener {
                viewModel.fetchRepository(repoName)
            }
            text = when (error) {
                is RepositoryError.NetworkError -> getString(R.string.retry_button_label)
                else -> getString(R.string.refresh_button_label)
            }
        }

        detailInfoErrorScreen.visibility = if (visibility)
            View.VISIBLE
        else
            View.INVISIBLE


        detailInfoErrorTitle.apply {
            text = when (error) {
                is RepositoryError.NetworkError -> getString(R.string.connection_error)
                else -> getString(R.string.error)
            }
            setTextColor(ContextCompat.getColor(requireContext(), R.color.error))
        }

        detailInfoErrorDescription.text = when (error) {
            is RepositoryError.NetworkError -> {
                getString(R.string.check_your_internet_connection)
            }
            else -> {
                getString(
                    R.string.error_repositories,
                    if (error is RepositoryError.Error) error.message else "",
                )
            }
        }

        detailInfoErrorImage.setImageDrawable(
            context?.let {
                when (error) {
                    is RepositoryError.NetworkError -> ContextCompat.getDrawable(it,
                        R.drawable.ic_no_connection)
                    is RepositoryError.Error -> ContextCompat.getDrawable(it, R.drawable.ic_error)
                    else -> ContextCompat.getDrawable(it, R.drawable.ic_error)
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_out_button, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_logout -> {
                viewModel.logOut()
                navController.navigate(R.id.action_detailFragment_to_authFragment)
                true
            }
            android.R.id.home -> {
                navController.navigate(R.id.action_detailFragment_to_repositoriesListFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = repoName
            setHomeAsUpIndicator(R.drawable.ic_arrow_left)
            setDisplayHomeAsUpEnabled(true)
            show()
        }
        markDownRenderer = Markwon.create(requireContext())
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    companion object {
        private const val REPOSITORY_NAME_KEY = "repoNameKey"

        fun createArguments(repoName: String): Bundle {
            return bundleOf(REPOSITORY_NAME_KEY to repoName)
        }
    }
}