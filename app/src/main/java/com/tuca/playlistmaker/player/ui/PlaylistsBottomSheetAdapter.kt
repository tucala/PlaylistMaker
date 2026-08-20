package com.tuca.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.databinding.PlaylistBottomSheetItemBinding
import com.tuca.playlistmaker.library.domain.models.Playlist
import java.io.File

class PlaylistsBottomSheetAdapter(
    private var playlists: List<Playlist>,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsBottomSheetAdapter.PlaylistViewHolder>() {

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistBottomSheetItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(
        private val binding: PlaylistBottomSheetItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.tvPlaylistName.text = playlist.name
            val count = playlist.tracksCount
            binding.tvPlaylistTracksCount.text = binding.root.context.resources.getQuantityString(
                R.plurals.tracks_count,
                count,
                count
            )

            val cornerRadius = binding.root.context.resources.getDimensionPixelSize(R.dimen.cover_corner_radius)

            if (!playlist.coverPath.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(File(playlist.coverPath))
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.ivPlaylistCover)
            } else {
                Glide.with(binding.root)
                    .load(R.drawable.ic_placeholder)
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .into(binding.ivPlaylistCover)
            }

            binding.root.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }
    }
}
