package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.bandit.R
import com.bandit.data.model.BandInvitation
import com.bandit.databinding.DialogFragmentBandInvitationBinding
import com.bandit.ui.adapter.BandInvitationAdapter
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.helper.TouchHelper
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
        with(binding) {
            bandInvitationSearchView.setOnQueryTextListener(this@BandInvitationDialogFragment)
            val touchHelper = TouchHelper<BandInvitation>(
                super.requireContext(),
                bandInvitationRvList,
                { bandInvitation -> onRejectBandInvitation(bandInvitation) },
                { bandInvitation -> onAcceptBandInvitation(bandInvitation) },
                R.drawable.ic_check_circle_outline_white,
                R.drawable.ic_remove_circle_outline_white
            )
            ItemTouchHelper(touchHelper).attachToRecyclerView(bandInvitationRvList)
            viewModel.bandInvitations.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    bandInvitationRvEmpty.visibility = View.GONE
                    bandInvitationRvList.visibility = View.VISIBLE
                    touchHelper.updateItems(it.sorted())
                    bandInvitationRvList.adapter = BandInvitationAdapter(
                        it.sorted(),
                        { bandInvitation -> onAcceptBandInvitation(bandInvitation) },
                        { bandInvitation -> onRejectBandInvitation(bandInvitation) }
                    )
                } else {
                    bandInvitationRvEmpty.visibility = View.VISIBLE
                    bandInvitationRvList.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.bandInvitationSearchView.setQuery("", false)
        _binding = null
        viewModel.refresh()
    }

    private fun onAcceptBandInvitation(bandInvitation: BandInvitation) {
        AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
            viewModel.acceptBandInvitation(bandInvitation)
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.band_invitation_accepted_toast)
            )
            super.dismiss()
        }
    }

    private fun onRejectBandInvitation(bandInvitation: BandInvitation) {
        AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
            viewModel.rejectBandInvitation(bandInvitation)
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.band_invitation_rejected_toast)
            )
            super.dismiss()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.band_invitation_filtered_toast)
        )
        binding.bandInvitationSearchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterBandInvitations(bandName = newText)
        viewModel.filterBandInvitationsName.value = newText
        return true
    }

    companion object {
        const val TAG = "BAND INVITATION DIALOG FRAGMENT"
    }

}