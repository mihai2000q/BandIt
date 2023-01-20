package com.bandit.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.databinding.FragmentHomeBinding
import com.bandit.di.DILocator
import com.bandit.ui.AccountDialogFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.generateHomeElements(
            viewModel.elements.value ?: mapOf(),
            binding.homeSvTableLayout,
            this.requireContext(),
            this.requireActivity().findViewById(R.id.main_bottom_navigation_view)
        )

        binding.homeBtAccount.setOnClickListener{
            AccountDialogFragment(binding.homeBtAccount).show(childFragmentManager, AccountDialogFragment.TAG)
            binding.homeBtAccount.setImageDrawable(
                ContextCompat.getDrawable(
                    this.requireContext(),
                    R.drawable.ic_baseline_account_clicked
                )
            )
        }

        binding.homeTvWelcome.text = "Welcome ${DILocator.authenticator.currentUser!!.displayName}, to"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}