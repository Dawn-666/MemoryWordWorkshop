package com.tencentmusic.memory.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tencentmusic.memory.ImportActivity
import com.tencentmusic.memory.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.iv_avatar).setOnClickListener {
            val input = EditText(it.context).apply {
                @SuppressLint("SetTextI18n")
                setText("http://172.19.8.221:5173")
                hint = "输入内容"
            }
            MaterialAlertDialogBuilder(it.context)
                .setTitle("对话框标题")
                .setView(input)
                .setPositiveButton("确定") { dialog, _ ->
                    // 处理用户输入
                    HomeFragment.testUrl = input.text.toString()
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        view.findViewById<View>(R.id.layout_import).setOnClickListener {
            startActivity(Intent(it.context, ImportActivity::class.java))
        }
    }
}