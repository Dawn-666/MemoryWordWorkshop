package com.tencentmusic.memory.data

data class WordBook(
    val id: String,
    val title: String,
    val coverRes: Int,
    val wordCount: Int,
    val learnedCount: Int,
    val category: String,
    val difficulty: String,
    val isStudying: Boolean = false,
    val lastStudyTime: Long = 0
) {
    val progress: Int
        get() = if (wordCount == 0) 0 else (learnedCount * 100 / wordCount)
}