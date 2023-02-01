package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Album
import com.bandit.databinding.ModelAlbumBinding
import com.bandit.ui.songs.SongsViewModel
import com.bandit.ui.songs.albums.AlbumDetailDialogFragment
import com.bandit.ui.songs.albums.AlbumEditDialogFragment
import com.bandit.util.AndroidUtils

data class AlbumAdapter(
    private val activity: FragmentActivity,
    private val albums: List<Album>,
    private val viewModel: SongsViewModel,
    private val childFragmentManager: FragmentManager
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    private val albumEditDialogFragment = AlbumEditDialogFragment()
    private val albumDetailDialogFragment = AlbumDetailDialogFragment()
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
            itemView.layoutParams.width = AndroidUtils.getScreenWidth(activity) * 7 / 16
            itemView.layoutParams.height = AndroidUtils.getScreenHeight(activity) / 4
            itemView.setOnClickListener { onClick(album) }
            itemView.setOnLongClickListener { onLongClick(holder, album) }
            with(binding) {
                albumName.text = album.name
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
                R.id.popup_menu_delete -> onDelete(holder, album)
                else -> onEdit(album)
            }
        }
    }

    private fun onClick(album: Album) {
        viewModel.selectedAlbum.value = album
        AndroidUtils.showDialogFragment(
            albumDetailDialogFragment,
            childFragmentManager
        )
    }

    private fun onLongClick(holder: AlbumAdapter.ViewHolder, album: Album): Boolean {
        popupMenu(holder, album)
        if(!isPopupShown) {
            isPopupShown = true
            popupMenu.show()
        }
        viewModel.selectedAlbum.value = album
        return true
    }

    private fun onDelete(holder: ViewHolder, album: Album): Boolean {
        AndroidUtils.toastNotification(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.album_remove_toast),
        )
        return viewModel.removeAlbum(album)
    }

    private fun onEdit(album: Album): Boolean {
        viewModel.selectedAlbum.value = album
        AndroidUtils.showDialogFragment(
            albumEditDialogFragment,
            childFragmentManager
        )
        return true
    }

}