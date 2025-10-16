package com.tencentmusic.memory.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tencentmusic.memory.app.BookFragment
import com.tencentmusic.memory.app.HomeFragment
import com.tencentmusic.memory.app.PlayerFragment
import com.tencentmusic.memory.app.ProfileFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4 // 对应4个页面

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> BookFragment()
            2 -> PlayerFragment()
            3 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}