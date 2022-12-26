package com.bandit.ui.songs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bandit.databinding.FragmentSongsBinding

class SongsFragment : Fragment() {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var viewModel: SongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongsBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this)[SongsViewModel::class.java]

        return binding.root
    }
}