package com.tuca.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuca.playlistmaker.R

class AdditionalInfoAdapter(
    private val items: List<AdditionalInfoItem>
) : RecyclerView.Adapter<AdditionalInfoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackInfo: TextView = itemView.findViewById(R.id.trackInfo)
        private val loadedTrackInfo: TextView = itemView.findViewById(R.id.loadedTrackInfo)

        fun bind(item: AdditionalInfoItem) {
            trackInfo.text = item.label
            loadedTrackInfo.text = item.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.additional_details_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

