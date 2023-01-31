package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
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
    private val childFragmentManager: FragmentManager
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    private val songEditDialogFragment = SongEditDialogFragment()
    private val songDetailDialogFragment = SongDetailDialogFragment()

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
            itemView.setOnClickListener { onClick(song) }
            itemView.setOnLongClickListener { onLongClick(holder, song) }
            with(binding) {
                songName.text = song.name
                songReleaseDate.text = song.releaseDate.print()
                AndroidUtils.ifNullHide(songAlbumName, song.albumName)
            }
        }
    }

    private fun popupMenu(holder: SongAdapter.ViewHolder, song: Song) {
        val popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> onDelete(holder, song)
                else -> onEdit(song)
            }
        }
        popupMenu.show()
    }

    private fun onClick(song: Song) {
        viewModel.selectedSong.value = song
        AndroidUtils.showDialogFragment(
            songDetailDialogFragment,
            childFragmentManager
        )
    }

    private fun onLongClick(holder: SongAdapter.ViewHolder, song: Song): Boolean {
        popupMenu(holder, song)
        viewModel.selectedSong.value = song
        return true
    }

    private fun onDelete(holder: ViewHolder, song: Song): Boolean {
        AndroidUtils.toastNotification(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.song_remove_toast),
        )
        return viewModel.removeSong(song)
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