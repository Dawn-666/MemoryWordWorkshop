package com.tencentmusic.memory

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ImportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_import)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 返回按钮
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 从音乐平台导入
        findViewById<View>(R.id.card_platform).setOnClickListener {
            showPlatformSelectionDialog()
        }

        // 手动创建歌单
        findViewById<View>(R.id.card_manual).setOnClickListener {
            showManualCreateDialog()
        }

        // 从文件导入
        findViewById<View>(R.id.card_file).setOnClickListener {
            showFileImportDialog()
        }
    }

    private fun showPlatformSelectionDialog() {
        val platforms = arrayOf("Spotify", "Apple Music", "YouTube Music", "网易云音乐", "QQ音乐")

        AlertDialog.Builder(this)
            .setTitle("选择音乐平台")
            .setItems(platforms) { dialog, which ->
                when (which) {
                    0 -> connectToSpotify()
                    1 -> connectToAppleMusic()
                    2 -> connectToYouTubeMusic()
                    3 -> connectToNetEaseMusic()
                    4 -> connectToQQMusic()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun connectToSpotify() {
        // 实现 Spotify 连接逻辑
        showLoading("正在连接 Spotify...")

        // 模拟连接过程
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
            showSuccessMessage("Spotify 连接成功！")
            // 跳转到歌单选择页面
            // startActivity(Intent(this, SpotifyPlaylistActivity::class.java))
        }, 2000)
    }

    private fun connectToAppleMusic() {
        showLoading("正在连接 Apple Music...")
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
            showSuccessMessage("Apple Music 连接成功！")
        }, 2000)
    }

    private fun connectToYouTubeMusic() {
        showLoading("正在连接 YouTube Music...")
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
            showSuccessMessage("YouTube Music 连接成功！")
        }, 2000)
    }

    private fun connectToNetEaseMusic() {
        showLoading("正在连接网易云音乐...")
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
            showSuccessMessage("网易云音乐连接成功！")
        }, 2000)
    }

    private fun connectToQQMusic() {
        showLoading("正在连接 QQ音乐...")
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
            showSuccessMessage("QQ音乐连接成功！")
        }, 2000)
    }

    private fun showManualCreateDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_song_list, null)
        val etPlaylistName = dialogView.findViewById<EditText>(R.id.et_playlist_name)

        AlertDialog.Builder(this)
            .setTitle("创建新歌单")
            .setView(dialogView)
            .setPositiveButton("创建") { dialog, which ->
                val playlistName = etPlaylistName.text.toString().trim()
                if (playlistName.isNotEmpty()) {
                    createManualPlaylist(playlistName)
                } else {
                    Toast.makeText(this, "请输入歌单名称", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()

        etPlaylistName.requestFocus()
        showKeyboard(etPlaylistName)
    }

    private fun createManualPlaylist(name: String) {
        showLoading("创建歌单中...")

        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
            showSuccessMessage("歌单 '$name' 创建成功！")

            // 跳转到歌曲添加页面
//            val intent = Intent(this, AddSongsActivity::class.java).apply {
//                putExtra("playlist_name", name)
//            }
//            startActivity(intent)
        }, 1500)
    }

    private fun showFileImportDialog() {
        val options = arrayOf("导入本地音乐文件", "导入 M3U 播放列表", "从网盘导入")

        AlertDialog.Builder(this)
            .setTitle("选择导入方式")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> importLocalMusicFiles()
                    1 -> importM3UPlaylist()
                    2 -> importFromCloudStorage()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun importLocalMusicFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "选择音乐文件"), REQUEST_CODE_SELECT_MUSIC)
    }

    private fun importM3UPlaylist() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "选择 M3U 文件"), REQUEST_CODE_SELECT_M3U)
    }

    private fun importFromCloudStorage() {
        // 实现网盘导入逻辑
        Toast.makeText(this, "网盘导入功能开发中", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(message: String) {
        // 显示加载对话框
        val dialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .create()
        dialog.show()
    }

    private fun hideLoading() {
        // 隐藏加载对话框
        // 实际实现中需要维护对话框引用
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SELECT_MUSIC -> {
                if (resultCode == RESULT_OK) {
                    // 处理选择的音乐文件
                    showSuccessMessage("音乐文件导入成功！")
                }
            }
            REQUEST_CODE_SELECT_M3U -> {
                if (resultCode == RESULT_OK) {
                    // 处理 M3U 文件
                    showSuccessMessage("播放列表导入成功！")
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_MUSIC = 1001
        private const val REQUEST_CODE_SELECT_M3U = 1002
    }

}