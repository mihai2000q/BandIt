package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Song
import com.bandit.databinding.ModelSongBinding
import com.bandit.extension.printName
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

data class SongAdapter(
    private val fragment: Fragment,
    private val songs: List<Song>,
    private val viewModel: SongsViewModel,
    private val onDeleteSong: ((Song) -> Unit)?,
    private val onEditSong: ((Song) -> Unit)?,
    private val onClick: (Song) -> Unit,
    private val deleteText: String = "",
    private val isLongClickable: Boolean = true
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {
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
            itemView.setOnClickListener { onClick.invoke(song) }
            itemView.setOnLongClickListener {
                if(isLongClickable)
                    onLongClick(holder, song)
                else
                    return@setOnLongClickListener true
            }
            with(binding) {
                songName.text = song.name
                songReleaseDate.text = song.releaseDate.printName()
                AndroidUtils.ifNullHide(songAlbumName, song.albumName)
            }
        }
    }

    private fun popupMenu(holder: SongAdapter.ViewHolder, song: Song) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        if(deleteText.isNotBlank())
            popupMenu.menu[1].title = deleteText
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> onDelete(song)
                else -> onEdit(song)
            }
        }
    }

    private fun onLongClick(holder: ViewHolder, song: Song): Boolean {
        popupMenu(holder, song)
        if(!isPopupShown) {
            popupMenu.show()
            isPopupShown = true
        }
        return true
    }

    private fun onDelete(song: Song): Boolean {
        onDeleteSong?.invoke(song)
        return true
    }

    private fun onEdit(song: Song): Boolean {
        onEditSong?.invoke(song)
        return true
    }

}