package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.databinding.FragmentConcertsBinding
import com.bandit.ui.BaseFragment
import com.bandit.ui.band.BandViewModel
import com.bandit.util.AndroidUtils

class ConcertsFragment : BaseFragment() {

    private var _binding: FragmentConcertsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

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
        val concertDetailDialogFragment = ConcertDetailDialogFragment()
        val concertEditDialogFragment = ConcertEditDialogFragment()
        with(binding) {
            bandViewModel.band.observe(viewLifecycleOwner) {
                concertsBtAdd.isEnabled = !it.isEmpty()
                concertsBtFilter.isEnabled = !it.isEmpty()
            }
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

            with(viewModel) {
                concerts.observe(viewLifecycleOwner) {
                    concertsList.adapter = ConcertAdapter(it.sorted(), { concert ->
                            selectedConcert.value = concert
                            AndroidUtils.showDialogFragment(
                                concertDetailDialogFragment,
                                childFragmentManager
                            )
                        },
                        { concert -> selectedConcert.value = concert; return@ConcertAdapter true },
                        { concert ->
                            AndroidUtils.toastNotification(
                                super.requireContext(),
                                resources.getString(R.string.concert_remove_toast),
                            )
                            return@ConcertAdapter removeConcert(concert)
                        }
                    ) { concert ->
                        selectedConcert.value = concert
                        AndroidUtils.showDialogFragment(
                            concertEditDialogFragment,
                            childFragmentManager
                        )
                        return@ConcertAdapter true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}