package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Song
import com.bandit.databinding.ModelSongBinding
import com.bandit.extension.print
import com.bandit.ui.songs.SongDetailDialogFragment
import com.bandit.ui.songs.SongEditDialogFragment
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

data class SongAdapter(
    private val songs: List<Song>,
    private val viewModel: SongsViewModel,
    private val childFragmentManager: FragmentManager,
    private val onDelete: (Song) -> Boolean,
    private val deleteText: String? = null,
    private val isLongClickable: Boolean = true,
    private val onClick: ((Song) -> Unit)? = null
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    private val songEditDialogFragment = SongEditDialogFragment()
    private val songDetailDialogFragment = SongDetailDialogFragment()
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false

    inner class ViewHolder(val binding: ModelSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]

        with(holder) {
            itemView.setOnClickListener {
            if(onClick == null)
                onClick(song)
            else
                onClick.invoke(song)
            }
            itemView.setOnLongClickListener {
                if(isLongClickable)
                    onLongClick(holder, song)
                else
                    return@setOnLongClickListener true
            }
            with(binding) {
                songName.text = song.name
                songReleaseDate.text = song.releaseDate.print()
                AndroidUtils.ifNullHide(songAlbumName, song.albumName)
            }
        }
    }

    private fun popupMenu(holder: SongAdapter.ViewHolder, song: Song) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        if(deleteText != null)
            popupMenu.menu[0].title = deleteText
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> {
                    popupMenu(holder, song)
                    onDelete(song)
                }
                else -> onEdit(song)
            }
        }
    }

    private fun onClick(song: Song) {
        viewModel.selectedSong.value = song
        AndroidUtils.showDialogFragment(
            songDetailDialogFragment,
            childFragmentManager
        )
    }

    private fun onLongClick(holder: ViewHolder, song: Song): Boolean {
        popupMenu(holder, song)
        if(!isPopupShown) {
            popupMenu.show()
            isPopupShown = true
        }
        viewModel.selectedSong.value = song
        return true
    }

    private fun onEdit(song: Song): Boolean {
        viewModel.selectedSong.value = song
        AndroidUtils.showDialogFragment(
            songEditDialogFragment,
            childFragmentManager
        )
        return true
    }

}