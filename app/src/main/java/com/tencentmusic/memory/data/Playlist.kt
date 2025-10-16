package com.tencentmusic.memory.data

data class Playlist(
    val name: String,
    val words: List<Word>,
    val isCurrent: Boolean = false
)