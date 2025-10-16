package com.tencentmusic.memory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.tencentmusic.memory.data.Database
import com.tencentmusic.memory.view.WordAdapter
import com.tencentmusic.memory.viewmodel.PlayerViewModel.Companion.playerViewModels

class WordActivity() : AppCompatActivity() {
    private val wordAdapter by lazy { WordAdapter(emptyList()) }
    private var currentPlaylistIndex = 0
    private var currentPlayingPosition = 0
    private val player by playerViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_word)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupUI()
        loadCurrentPlaylist()
        findViewById<ImageView>(R.id.btn_play_pause).let {
            player.playing.observe(this) { playing ->
                it.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
            }
            it.setOnClickListener {
                player.toggle()
            }
        }
        findViewById<TextView>(R.id.tv_current_word).let {
            player.currentWord.observe(this) { word ->
                it.text = word?.word ?: "暂未播放"
            }
        }
        // 高亮显示正在播放的单词
        player.currentWord.observe(this) { word ->
            wordAdapter.updatePlayingWord(word)
        }
    }

    private fun setupUI() {
        // 设置工具栏
        // setSupportActionBar(toolbar)
        // 返回按钮
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
        // 添加按钮
        findViewById<View>(R.id.btn_add).setOnClickListener {
            showCreateListDialog()
        }
        // 初始化单词列表
        findViewById<RecyclerView>(R.id.rv_words).apply {
            // addItemDecoration(DividerItemDecoration(this@WordActivity, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@WordActivity)
            adapter = wordAdapter.apply {

                // 单词播放点击事件
                wordAdapter.setOnWordPlayClickListener { position ->
                    playWordAtPosition(position)
                }

                // 收藏点击事件
                wordAdapter.setOnFavoriteClickListener { position, isFavorite ->
                    toggleFavorite(position, isFavorite)
                }

            }
            setupTabs(this)
        }
    }

    private fun setupTabs(rv: RecyclerView) {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        // 清除现有tab
        tabLayout.removeAllTabs()

        // 添加tab
        Database.playlists.forEachIndexed { index, playlist ->
            tabLayout.addTab(tabLayout.newTab().setText(playlist.name))
        }

        // Tab选择监听
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentPlaylistIndex = tab.position
                loadCurrentPlaylist()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // 默认选择第一个tab
        // tabLayout.getTabAt(currentPlaylistIndex)?.select()

        tabLayout.getTabAt(player.playlistIndex)?.select()
        val manager = rv.layoutManager as? LinearLayoutManager ?: return
        val word = player.currentWord.value ?: return
        val position = player.playlist.value?.indexOfFirst {
            it == word
        } ?: 0
        if (position !in manager.findFirstVisibleItemPosition()..manager.findLastVisibleItemPosition()) {
            manager.scrollToPosition(position)
        }
    }

    private fun loadCurrentPlaylist() {
        val currentPlaylist = Database.playlists.getOrNull(currentPlaylistIndex) ?: return

        // 更新列表信息
        // tvListInfo.text = "${currentPlaylist.name}·${currentPlaylist.words.size}个单词"

        // 更新单词列表
        wordAdapter.updateData(currentPlaylist.words)

        // 如果没有播放进度，默认定位在第一个单词
        if (currentPlayingPosition >= currentPlaylist.words.size) {
            currentPlayingPosition = 0
        }
    }

    private fun playWordAtPosition(position: Int) {
        val currentPlaylist = Database.playlists.getOrNull(currentPlaylistIndex) ?: return
        player.loadPlaylist(currentPlaylist.words, position)
        player.playlistIndex = currentPlaylistIndex
        player.toggle()
    }

    private fun toggleFavorite(position: Int, isFavorite: Boolean) {
        val currentPlaylist = Database.playlists.getOrNull(currentPlaylistIndex) ?: return
        if (position < currentPlaylist.words.size) {
            // 在实际应用中，这里应该调用API或更新数据库
            // 由于playlists是val不可变，这里只是模拟更新
            // 显示收藏状态变化
            val word = currentPlaylist.words[position]
            val message = if (isFavorite) "已收藏: ${word.word}" else "取消收藏: ${word.word}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            // 在实际应用中，需要更新数据源并通知适配器
            // 这里简单重新加载当前播放列表来模拟更新
            loadCurrentPlaylist()
        }
    }

    private fun showCreateListDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_word_list, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.et_list_name)
        AlertDialog.Builder(this)
            .setTitle("创建新列表")
            .setView(dialogView)
            .setPositiveButton("创建") { dialog, which ->
                val listName = editTextName.text.toString().trim()
                if (listName.isNotEmpty()) {
                    createNewPlaylist(listName)
                } else {
                    Toast.makeText(this, "请输入列表名称", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
        // 自动弹出键盘
        editTextName.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun createNewPlaylist(name: String) {
        // 在实际应用中，这里应该调用API创建新列表
        Toast.makeText(this, "创建列表: $name", Toast.LENGTH_SHORT).show()
        // 创建成功后，可以刷新tab显示
        // 这里只是示例，实际需要更新数据源
    }
}