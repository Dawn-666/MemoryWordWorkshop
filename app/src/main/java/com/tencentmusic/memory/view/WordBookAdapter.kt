package com.tencentmusic.memory.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tencentmusic.memory.R
import com.tencentmusic.memory.data.WordBook

class WordBookAdapter(
    private var wordBooks: List<WordBook> = emptyList(),
    private val onItemClick: (WordBook) -> Unit
) : RecyclerView.Adapter<WordBookAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvWordCount: TextView = itemView.findViewById(R.id.tv_word_count)
        private val tvProgress: TextView = itemView.findViewById(R.id.tv_progress)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        fun bind(wordBook: WordBook) {
            ivCover.setImageResource(wordBook.coverRes)
            tvTitle.text = wordBook.title
            tvWordCount.text = "${wordBook.wordCount}个单词"
            tvProgress.text = "已学 ${wordBook.progress}%"
            progressBar.progress = wordBook.progress

            when {
                wordBook.isStudying -> {
                    tvStatus.text = "学习中"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_studying)
                }
                wordBook.learnedCount == wordBook.wordCount -> {
                    tvStatus.text = "已学完"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_completed)
                }
                wordBook.learnedCount > 0 -> {
                    tvStatus.text = "进行中"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_in_progress)
                }
                else -> {
                    tvStatus.text = "未开始"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_not_started)
                }
            }

            itemView.setOnClickListener { onItemClick(wordBook) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_book_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wordBooks[position])
    }

    override fun getItemCount(): Int = wordBooks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newWordBooks: List<WordBook>) {
        this.wordBooks = newWordBooks
        notifyDataSetChanged()
    }

    fun filterByCategory(category: String) {
        val filtered = if (category == "全部") {
            wordBooks
        } else {
            wordBooks.filter { it.category == category }
        }
        updateData(filtered)
    }
}