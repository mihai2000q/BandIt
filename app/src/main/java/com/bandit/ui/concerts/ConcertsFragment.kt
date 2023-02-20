package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.databinding.FragmentConcertsBinding
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.util.AndroidUtils

class ConcertsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentConcertsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentConcertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val concertAddDialogFragment = ConcertAddDialogFragment()
        val concertFilterDialogFragment = ConcertFilterDialogFragment()
        with(binding) {
            AndroidComponents.header(
                super.requireActivity(),
                concertsHeader.headerBtAccount
            )
            concertsSearchView.setOnQueryTextListener(this@ConcertsFragment)
            concertsHeader.headerTvTitle.setText(R.string.title_concerts)
            concertsBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    concertAddDialogFragment,
                    childFragmentManager
                )
            }
            concertsBtFilter.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    concertFilterDialogFragment,
                    childFragmentManager
                )
            }
            viewModel.concerts.observe(viewLifecycleOwner) {
                concertsList.adapter = ConcertAdapter(
                    this@ConcertsFragment,
                    it.sorted(),
                    viewModel
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterConcerts(name = newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.concert_filter_toast)
        )
        binding.concertsSearchView.clearFocus()
        return false
    }
}