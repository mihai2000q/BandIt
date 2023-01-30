package com.bandit.ui.concerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.databinding.FragmentConcertsBinding
import com.bandit.ui.account.AccountDialogFragment
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.band.CreateBandDialogFragment
import com.bandit.util.AndroidUtils

class ConcertsFragment : Fragment() {

    private var _binding: FragmentConcertsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConcertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val concertAddDialogFragment = ConcertAddDialogFragment()
        val concertFilterDialogFragment = ConcertFilterDialogFragment()
        val concertDetailDialogFragment = ConcertDetailDialogFragment()
        val concertEditDialogFragment = ConcertEditDialogFragment()
        val accountDialogFragment = AccountDialogFragment(binding.concertsBtAccount)
        val createBandDialogFragment = CreateBandDialogFragment()
        val bandDialogFragment = BandDialogFragment()
        with(binding) {
            bandViewModel.band.observe(viewLifecycleOwner) {
                concertsBtAdd.isEnabled = !it.isEmpty()
                concertsBtFilter.isEnabled = !it.isEmpty()
            }
            AndroidUtils.accountButton(
                super.requireActivity(),
                concertsBtAccount,
                accountDialogFragment
            )
            AndroidUtils.bandButton(
                super.requireActivity(),
                concertsBtBand,
                bandViewModel.band,
                viewLifecycleOwner,
                createBandDialogFragment,
                bandDialogFragment
            )
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