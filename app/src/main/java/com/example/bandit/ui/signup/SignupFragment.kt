package com.example.bandit.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bandit.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private lateinit var _binding: FragmentSignupBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}