package ru.kheynov.icerockgithubviewer.presentation.screens.detail_info

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.FragmentDetailInfoBinding

@AndroidEntryPoint
class DetailInfoFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentDetailInfoBinding

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
        return binding.root
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
            title = "TODO"
            setHomeAsUpIndicator(R.drawable.ic_arrow_left)
            setDisplayHomeAsUpEnabled(true)
            show()
        }
    }
}