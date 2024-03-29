package com.example.playlistmaker.common.presentation

import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.PLAY_LISTS_IMAGES_DIRECTORY
import com.example.playlistmaker.common.utils.DiffCallback
import com.example.playlistmaker.common.models.PlayList
import java.io.File

abstract class PlayListsAdapter(private val clickListener: PlayListClickListener) :
    RecyclerView.Adapter<PlayListViewHolder>() {

    var playLists = listOf<PlayList>()
        set(newList) {
            val diffResult = DiffUtil.calculateDiff(
                object : DiffCallback<PlayList>(field, newList) {
                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return field[oldItemPosition].playListId == newList[newItemPosition].playListId
                                && field[oldItemPosition].image == newList[newItemPosition].image
                                && field[oldItemPosition].name == newList[newItemPosition].name
                                && field[oldItemPosition].tracksCount == newList[newItemPosition].tracksCount
                    }
                }
            )
            field = newList
            diffResult.dispatchUpdatesTo(this)
        }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder

    override fun getItemCount(): Int = playLists.size

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.bind(playLists[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(playLists[holder.adapterPosition])
        }
    }

    fun interface PlayListClickListener {
        fun onClick(playList: PlayList)
    }
}

class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playListImage: ImageView = itemView.findViewById(R.id.playListImage)
    private val playListName: TextView = itemView.findViewById(R.id.playListName)
    private val playListCountTracks: TextView = itemView.findViewById(R.id.playListTracksCount)

    fun bind(playList: PlayList) {
        playListName.text = playList.name
        playListCountTracks.text = playListCountTracks.resources.getQuantityString(
            R.plurals.plural_count_tracks, playList.tracksCount, playList.tracksCount
        )
        val filePath = File(
            itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAY_LISTS_IMAGES_DIRECTORY
        )

        Glide
            .with(itemView)
            .load(playList.image?.let { imageName -> File(filePath, imageName) })
            .placeholder(R.drawable.ic_placeholder)
            .into(playListImage)
    }
}