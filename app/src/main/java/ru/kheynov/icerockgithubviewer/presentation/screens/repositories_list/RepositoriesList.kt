package ru.kheynov.icerockgithubviewer.presentation.screens.repositories_list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.FragmentRepositoriesListBinding


class RepositoriesList : Fragment() {

    private lateinit var navController: NavController

    private lateinit var binding: FragmentRepositoriesListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRepositoriesListBinding.inflate(
            inflater,
            container,
            false,
        )

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Repositories"
            setDisplayHomeAsUpEnabled(false)
            show()
        }
        navController = Navigation.findNavController(view)

        binding.repositoriesListLabel.setOnClickListener {
            navController.navigate(R.id.action_repositoriesListFragment_to_detailFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_out_button, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_action_logout){
            navController.navigate(R.id.action_repositoriesListFragment_to_authFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}