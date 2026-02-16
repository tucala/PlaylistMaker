package com.tuca.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    private val tvArtistTime: TextView = itemView.findViewById(R.id.tv_artist_time)

    fun bind(track: SearchActivity.Track) {
        tvTitle.text = track.trackName
        tvArtistTime.text = "${track.artistName} • ${track.trackTime}"

        val context = itemView.context
        val cornerRadius = context.resources.getDimensionPixelSize(R.dimen.cover_corner_radius)

        val options = RequestOptions()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(RoundedCorners(cornerRadius))

        Glide.with(context)
            .load(track.artworkUrl100)
            .apply(options)
            .into(ivCover)
    }
}
