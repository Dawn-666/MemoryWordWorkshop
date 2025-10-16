package com.tencentmusic.memory.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencentmusic.memory.R
import com.tencentmusic.memory.data.Word

class PlaylistAdapter(private var words: List<Word>, private var currentPosition: Int = 0) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    private var onWordClick: ((Int) -> Unit)? = null
    // var onFavoriteClick: ((Int, Boolean) -> Unit)? = null

    fun setOnWordClickListener(listener: ((Int) -> Unit)?) {
        onWordClick = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvWord: TextView = itemView.findViewById(R.id.tv_word_item)
        private val tvMeaning: TextView = itemView.findViewById(R.id.tv_meaning_item)
        private val btnPlay: ImageView = itemView.findViewById(R.id.btn_play_item)
        // private val btnFavorite: ImageView = itemView.findViewById(R.id.btn_favorite_item)
        // private val currentIndicator: View = itemView.findViewById(R.id.current_indicator)

        fun bind(word: Word, position: Int) {
            tvWord.text = word.word
            tvMeaning.text = word.meaning

            // 高亮显示当前播放的单词
            itemView.isSelected = position == currentPosition
            // currentIndicator.visibility = if (position == currentPosition) View.VISIBLE else View.INVISIBLE

            // 收藏按钮状态
            // btnFavorite.setImageResource(
            //     if (word.isFavorite) R.drawable.ic_favorite_filled
            //     else R.drawable.ic_favorite_border
            // )

            // 播放按钮点击事件
            btnPlay.setOnClickListener {
                onWordClick?.invoke(position)
            }

            // 收藏按钮点击事件
            // btnFavorite.setOnClickListener {
            //     val newFavoriteState = !word.isFavorite
            //     onFavoriteClick?.invoke(position, newFavoriteState)
            // }

            // 整个项点击事件
            itemView.setOnClickListener {
                onWordClick?.invoke(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_play_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(words[position], position)
    }

    override fun getItemCount() = words.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newWords: List<Word>, newCurrentPosition: Int = 0) {
        words = newWords
        currentPosition = newCurrentPosition
        notifyDataSetChanged()
    }

    fun updateCurrentPosition(position: Int) {
        notifyItemChanged(currentPosition)
        currentPosition = position
        notifyItemChanged(position)
    }
}
