package ru.kheynov.icerockgithubviewer.presentation.screens.repositories_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kheynov.icerockgithubviewer.databinding.FragmentRepositoriesListBinding


class RepositoriesList : Fragment() {

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

}