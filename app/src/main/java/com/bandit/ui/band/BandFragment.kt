package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.databinding.FragmentBandBinding
import com.bandit.ui.adapter.BandAdapter
import com.bandit.util.AndroidUtils

class BandFragment : Fragment() {
    private var _binding: FragmentBandBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBandBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val bandAddMemberDialogFragment = BandAddMemberDialogFragment()
            AndroidUtils.ifNullHide(bandTvName, viewModel.band.value?.name)
            viewModel.members.observe(viewLifecycleOwner) {
                bandRvMemberList.adapter = BandAdapter(it)
            }
            bandBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    bandAddMemberDialogFragment,
                    childFragmentManager
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}