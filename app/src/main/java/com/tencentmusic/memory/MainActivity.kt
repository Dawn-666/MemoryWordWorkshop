package com.tencentmusic.memory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tencentmusic.memory.view.MainPagerAdapter
import kotlin.math.abs
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        // 设置适配器
        viewPager.adapter = MainPagerAdapter(this)
        // 禁用用户滑动切换页面（可选）
        viewPager.isUserInputEnabled = false
        // 设置页面切换动画（可选）
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        // 设置底部导航项选择监听
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_word -> viewPager.currentItem = 1
                R.id.nav_play -> viewPager.currentItem = 2
                R.id.nav_user -> viewPager.currentItem = 3
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        // 监听 ViewPager2 页面变化，更新底部导航选中状态
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNav.menu[position].isChecked = true
            }
        })
        // 默认选中首页
        viewPager.currentItem = 0
    }

    // 页面切换动画（可选）
    private class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height

                when {
                    position < -1 -> // [-Infinity,-1)
                        alpha = 0f

                    position <= 1 -> { // [-1,1]
                        val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2

                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        scaleX = scaleFactor
                        scaleY = scaleFactor
                        alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
                    }

                    else -> // (1,+Infinity]
                        alpha = 0f
                }
            }
        }

        companion object {
            private const val MIN_SCALE = 0.85f
            private const val MIN_ALPHA = 0.5f
        }
    }
}