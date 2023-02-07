package com.bandit.ui.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bandit.databinding.FragmentPersonalNotesBinding

class PersonalNotesFragment : Fragment() {

    private var _binding: FragmentPersonalNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonalNotesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalNotesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}