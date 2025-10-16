package com.tencentmusic.memory.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.tencentmusic.memory.WordActivity
import com.tencentmusic.memory.R
import com.tencentmusic.memory.view.PlaylistAdapter
import com.tencentmusic.memory.viewmodel.PlayerViewModel.Companion.playerViewModels

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private val player by playerViewModels()
    private val btnPlayPause by lazy { view?.findViewById<ImageView>(R.id.btn_play_pause) }
    private val btnPrev by lazy { view?.findViewById<View>(R.id.btn_prev) }
    private val btnNext by lazy { view?.findViewById<View>(R.id.btn_next) }
    private val tvWord by lazy { view?.findViewById<TextView>(R.id.tv_word) }
    private val tvPhonetic by lazy { view?.findViewById<TextView>(R.id.tv_phonetic) }
    private val tvMeaning by lazy { view?.findViewById<TextView>(R.id.tv_meaning) }
    private val tvTotalTime by lazy { view?.findViewById<TextView>(R.id.tv_total_time) }
    private val tvCurrentTime by lazy { view?.findViewById<TextView>(R.id.tv_current_time) }
    private val seekbar by lazy { view?.findViewById<SeekBar>(R.id.seekbar) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI(view)
        player.playlist.observe(viewLifecycleOwner) { words ->
            tvTotalTime?.text = getString(R.string.format_total, words.size)
            playlistAdapter.updateData(words)
            seekbar?.max = words.size
        }
        player.currentIndex.observe(viewLifecycleOwner) { index ->
            if (index != seekbar?.max) {
                tvCurrentTime?.text = getString(R.string.format_current, index + 1)
            }
            playlistAdapter.updateCurrentPosition(index)
            seekbar?.progress = index
        }
        player.currentWord.observe(viewLifecycleOwner) { word ->
            tvWord?.text = word?.word ?: "播放列表为空"
            tvPhonetic?.text = word?.phonetic ?: "--"
            tvMeaning?.text = word?.meaning ?: "请先选择播放单词"
        }
    }

    private val playlistAdapter by lazy { PlaylistAdapter(emptyList(), player.currentIndex.value ?: 0) }

    private fun setupUI(view: View) {
        // 设置工具栏
        // setSupportActionBar(binding.toolbar)
        // 播放列表设置按钮
        view.findViewById<View>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(requireContext(), WordActivity::class.java))//.putExtra("word", player.currentWord.value?.word))
        }
        view.findViewById<RecyclerView>(R.id.rv_playlist).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
        }
        // 播放列表点击事件
        playlistAdapter.setOnWordClickListener { position ->
            player.seek(position, false)
        }
        // 收藏点击事件
        // playlistAdapter.onFavoriteClick = { position, isFavorite ->
        //     toggleFavorite(position, isFavorite)
        // }
        // 播放选项监听
        view.findViewById<SwitchMaterial>(R.id.switch_english).let {
            player.playEnglish.observe(viewLifecycleOwner) { checked ->
                it.isChecked = checked
            }
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                try {
                    player.setPlayEnglish(isChecked)
                } catch (_: IllegalArgumentException) {
                    it.isChecked = true
                }
            }
        }
        view.findViewById<SwitchMaterial>(R.id.switch_spelling).let {
            player.playSpelling.observe(viewLifecycleOwner) { checked ->
                it.isChecked = checked
            }
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                try {
                    player.setPlaySpelling(isChecked)
                } catch (_: IllegalArgumentException) {
                    it.isChecked = true
                }
            }
        }
        view.findViewById<SwitchMaterial>(R.id.switch_chinese).let {
            player.playChinese.observe(viewLifecycleOwner) { checked ->
                it.isChecked = checked
            }
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                try {
                    player.setPlayChinese(isChecked)
                } catch (_: IllegalArgumentException) {
                    it.isChecked = true
                }
            }
        }
        // 进度条监听
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var state = false

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seek(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                state = player.playing.value == true
                player.pausePlay()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player.seek(seekBar.progress, if (state) true else null)
            }
        })
        // 播放/暂停按钮
        btnPlayPause?.let {
            player.playing.observe(viewLifecycleOwner) { playing ->
                it.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
            }
            it.setOnClickListener {
                player.toggle()
            }
        }
        // 上一词按钮
        btnPrev?.setOnClickListener {
            player.speakPreviousWord()
        }
        // 下一词按钮
        btnNext?.setOnClickListener {
            player.speakNextWord()
        }
    }

//    private fun toggleFavorite(position: Int, isFavorite: Boolean) {
//        // 更新数据模型的收藏状态
//        // 这里应该调用API或更新数据库
//        val words = currentPlaylist?.words?.toMutableList() ?: return
//        if (position < words.size) {
//            words[position] = words[position].copy(isFavorite = isFavorite)
//            currentPlaylist = currentPlaylist?.copy(words = words)
//            playlistAdapter.updateData(words, currentWordIndex)
//        }
//    }
}