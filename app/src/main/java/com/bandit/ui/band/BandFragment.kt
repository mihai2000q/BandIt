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
            val bandCreateBandDialogFragment = BandCreateDialogFragment()
            viewModel.members.observe(viewLifecycleOwner) {
                bandTvName.text = viewModel.band.value?.name
                if(it.isEmpty()) {
                    bandRvMemberList.visibility = View.GONE
                    bandBtCreate.visibility = View.VISIBLE
                    bandHorizontalLayout.visibility = View.INVISIBLE
                }
                else {
                    bandBtCreate.visibility = View.GONE
                    bandRvMemberList.visibility = View.VISIBLE
                    bandHorizontalLayout.visibility = View.VISIBLE
                    bandRvMemberList.adapter = BandAdapter(it)
                }
            }
            bandBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    bandAddMemberDialogFragment,
                    childFragmentManager
                )
            }
            bandBtCreate.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    bandCreateBandDialogFragment,
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