package com.bandit.ui.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Album
import com.bandit.databinding.ModelAlbumBinding
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

data class AlbumAdapter(
    private val fragment: Fragment,
    private val albums: List<Album>,
    private val viewModel: SongsViewModel,
    private val onDeleteAlbum: (Album) -> Unit,
    private val onEditAlbum: (Album) -> Unit,
    private val onClickAlbum: (Album) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false

    inner class ViewHolder(val binding: ModelAlbumBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = albums[position]

        with(holder) {
            itemView.setOnClickListener { onClickAlbum(album) }
            itemView.setOnLongClickListener { onLongClick(holder, album) }
            with(binding) {
                albumName.text = album.name
                albumName.minWidth =
                    AndroidUtils.getScreenWidth(fragment.requireActivity()) * 7 / 16
                albumName.minimumHeight =
                    AndroidUtils.getScreenHeight(fragment.requireActivity()) / 4
            }
        }
    }

    private fun popupMenu(holder: ViewHolder, album: Album) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> onDelete(album)
                else -> onEdit(album)
            }
        }
    }

    private fun onLongClick(holder: AlbumAdapter.ViewHolder, album: Album): Boolean {
        popupMenu(holder, album)
        if(!isPopupShown) {
            isPopupShown = true
            popupMenu.show()
        }
        return true
    }

    private fun onDelete(album: Album): Boolean {
        onDeleteAlbum(album)
        return true
    }

    private fun onEdit(album: Album): Boolean {
        onEditAlbum(album)
        return true
    }
}