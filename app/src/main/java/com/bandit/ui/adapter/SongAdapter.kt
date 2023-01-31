package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Song
import com.bandit.databinding.ModelSongBinding

data class SongAdapter(
    private val list: List<Song>,
    private val onClick: (Song) -> Unit,
    private val onLongClick: (Song) -> Boolean,
    private val onDeleteItem: (Song) -> Boolean,
    private val onEditItem: (Song) -> Boolean
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

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
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = list[position]

        with(holder) {
            itemView.setOnClickListener { onClick(song) }
            itemView.setOnLongClickListener {
                popupMenu(holder, song)
                onLongClick(song)
            }
            with(binding) {
                songName.text = song.name
                songAlbumName.text = song.albumName
            }
        }
    }

    private fun popupMenu(holder: SongAdapter.ViewHolder, song: Song) {
        val popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> {
                    onDeleteItem(song)
                }
                else -> {
                    onEditItem(song)
                }
            }
        }
        popupMenu.show()
    }

}