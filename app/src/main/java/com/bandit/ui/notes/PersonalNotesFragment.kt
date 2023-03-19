package com.bandit.ui.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.data.model.Note
import com.bandit.databinding.FragmentPersonalNotesBinding
import com.bandit.ui.account.AccountViewModel
import com.bandit.ui.adapter.NoteAdapter
import com.bandit.util.AndroidUtils

class PersonalNotesFragment : Fragment() {

    private var _binding: FragmentPersonalNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonalNotesViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidUtils.setupRefreshLayout(this@PersonalNotesFragment, personalNotesRvList)
            personalNotesBtAdd.setOnClickListener { this@PersonalNotesFragment.addNote() }
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.notes,
                personalNotesRvList,
                personalNotesRvEmpty
            ) {
                return@setRecyclerViewEmpty NoteAdapter(
                    this@PersonalNotesFragment,
                    it.sorted().asReversed(),
                    viewModel
                )
            }
            AndroidUtils.setupFabScrollUp(
                super.requireContext(),
                personalNotesRvList,
                personalNotesBtScrollUp
            )
            personalNotesRvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                val zoomOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_out)
                val zoomInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in_delay)
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if(newState != RecyclerView.SCROLL_STATE_SETTLING) {
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            binding.personalNotesBtAdd.startAnimation(zoomOutAnim)
                        } else {
                            binding.personalNotesBtAdd.startAnimation(zoomInAnim)
                        }
                    }
                }
            })
        }
    }

    private fun addNote() {
        AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
            viewModel.addNote(
                Note(
                    resources.getString(R.string.et_title),
                    resources.getString(R.string.default_note_message),
                    accountViewModel.account.value!!.id
                )
            )
        }
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.note_add_toast)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}