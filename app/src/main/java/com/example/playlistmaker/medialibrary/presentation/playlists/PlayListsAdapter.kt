package com.example.playlistmaker.medialibrary.presentation.playlists

import android.os.Environment
import android.view.LayoutInflater
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
import com.example.playlistmaker.medialibrary.domain.models.PlayList
import java.io.File

class PlayListsAdapter(private val clickListener: PlayListClickListener) : RecyclerView.Adapter<PlayListViewHolder>() {

    var playLists = listOf<PlayList>()
        set(newList) {
            val diffResult = DiffUtil.calculateDiff(
                //DiffCallback(field, newList)
                object:DiffCallback<PlayList>(field, newList){
                    override fun areContentsTheSame(oldItemPosition: Int,newItemPosition: Int)
                    : Boolean {
                        return field[oldItemPosition].playListId == newList[newItemPosition].playListId
                    }
                }
            )
            field = newList
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_grid, parent, false)
        return PlayListViewHolder(view)
    }

    override fun getItemCount(): Int = playLists.size

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.bind(playLists[position])
    }
}

class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playListImage: ImageView = itemView.findViewById(R.id.playListImage)
    private val playListName: TextView = itemView.findViewById(R.id.playListNameTextView)
    private val playListCountTracks: TextView = itemView.findViewById(R.id.playListCountTracksTextView)

    fun bind(playList: PlayList) {
        playListName.text = playList.name
        playListCountTracks.text = playListCountTracks.resources.getQuantityString(
            R.plurals.plural_count_tracks, playList.tracksCount, playList.tracksCount
        )
        val filePath =
            File(
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

fun interface PlayListClickListener {
    fun onClick(playList: PlayList)
}
