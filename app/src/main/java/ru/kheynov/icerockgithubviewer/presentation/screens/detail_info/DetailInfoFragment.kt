package ru.kheynov.icerockgithubviewer.presentation.screens.detail_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kheynov.icerockgithubviewer.databinding.FragmentDetailInfoBinding

class DetailInfoFragment : Fragment() {

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

}