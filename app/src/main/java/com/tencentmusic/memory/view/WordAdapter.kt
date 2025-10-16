package com.tencentmusic.memory.view

import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tencentmusic.memory.R
import com.tencentmusic.memory.data.Database
import com.tencentmusic.memory.data.Word

class WordAdapter(private var words: List<Word>) : RecyclerView.Adapter<WordAdapter.ViewHolder>() {
    private val favorites = HashSet<String>()
    private var playingWord: String? = null
    private var onWordPlayClick: ((Int) -> Unit)? = null
    private var onFavoriteClick: ((Int, Boolean) -> Unit)? = null

    init {
        favorites.addAll(Database.favorites)
    }

    fun setOnWordPlayClickListener(listener: ((Int) -> Unit)?) {
        onWordPlayClick = listener
    }

    fun setOnFavoriteClickListener(listener: ((Int, Boolean) -> Unit)?) {
        onFavoriteClick = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvWord: TextView = itemView.findViewById(R.id.tv_word_item)
        private val tvMeaning: TextView = itemView.findViewById(R.id.tv_meaning_item)
        private val btnPlay: ImageView = itemView.findViewById(R.id.btn_play_item)
        private val btnFavorite: ImageView = itemView.findViewById(R.id.btn_favorite_item)
        // private val playingIndicator: View = itemView.findViewById(R.id.playing_indicator)

        fun bind(word: Word, position: Int) {
            tvWord.text = word.word
            tvMeaning.text = word.meaning

            // 高亮显示正在播放的单词
            if (playingWord == word.word) {
                tvWord.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_primary))
                tvMeaning.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_primary))
            } else {
                tvWord.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                tvMeaning.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_dark))
            }

            // 播放按钮状态
            btnPlay.setGrayscale(playingWord != word.word)
            // 播放按钮点击事件
            btnPlay.setOnClickListener {
                onWordPlayClick?.invoke(position)
            }

            // 收藏按钮状态
            btnFavorite.isSelected = favorites.contains(word.word)
            // 收藏按钮点击事件
            btnFavorite.setOnClickListener {
                val word = word.word
                val state = favorites.contains(word)
                if (if (state) favorites.remove(word) else favorites.add(word)) {
                    onFavoriteClick?.invoke(position, !state)
                }
            }

            // 整个项点击事件（也可以触发播放）
            itemView.setOnClickListener {
                onWordPlayClick?.invoke(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_word_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(words[position], position)
    }

    override fun getItemCount(): Int = words.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newWords: List<Word>) {
        words = newWords
        notifyDataSetChanged()
    }

    fun updatePlayingWord(word: Word?) {
        val oldIndex = words.indexOfFirst {
            it.word == playingWord
        }
        playingWord = word?.word
        val newIndex = words.indexOfFirst {
            it.word == playingWord
        }
        if (oldIndex != newIndex) {
            if (oldIndex in words.indices) {
                notifyItemChanged(oldIndex)
            }
            if (newIndex in words.indices) {
                notifyItemChanged(newIndex)
            }
        }
    }

    companion object {
        fun ImageView.setGrayscale(enabled: Boolean) {
            colorFilter = if (enabled) ColorMatrixColorFilter(ColorMatrix().apply {
                setSaturation(0f)
            }) else null
        }
    }
}