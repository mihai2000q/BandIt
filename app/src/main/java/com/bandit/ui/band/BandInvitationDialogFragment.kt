package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.DialogFragmentBandInvitationBinding
import com.bandit.ui.adapter.BandInvitationAdapter
import com.bandit.util.AndroidUtils

class BandInvitationDialogFragment : DialogFragment(), OnQueryTextListener {

    private var _binding: DialogFragmentBandInvitationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBandInvitationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
        binding.bandInvitationSearch.setOnQueryTextListener(this)
        viewModel.bandInvitations.observe(viewLifecycleOwner) {
            binding.bandInvitationRvList.adapter = BandInvitationAdapter(
                this,
                it.sorted(),
                viewModel
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.refresh()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.band_invitation_filtered_toast)
        )
        binding.bandInvitationSearch.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterBandInvitations(bandName = newText)
        return true
    }

    companion object {
        const val TAG = "BAND INVITATION DIALOG FRAGMENT"
    }

}