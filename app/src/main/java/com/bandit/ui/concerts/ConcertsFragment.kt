package com.bandit.ui.concerts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bandit.databinding.FragmentConcertsBinding

class ConcertsFragment : Fragment() {

    private lateinit var binding: FragmentConcertsBinding
    private lateinit var viewModel: ConcertsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConcertsBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this)[ConcertsViewModel::class.java]

        return binding.root
    }

}