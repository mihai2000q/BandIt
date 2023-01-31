package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bandit.data.model.Album
import com.bandit.databinding.ModelAlbumBinding

data class AlbumAdapter(
    private val albums: List<Album>
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
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

    }

}