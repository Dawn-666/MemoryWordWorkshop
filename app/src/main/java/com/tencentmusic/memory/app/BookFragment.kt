package com.tencentmusic.memory.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.tencentmusic.memory.R
import com.tencentmusic.memory.data.Database
import com.tencentmusic.memory.data.WordBook
import com.tencentmusic.memory.view.GridSpacingItemDecoration
import com.tencentmusic.memory.view.WordBookAdapter

class BookFragment : Fragment(R.layout.fragment_book) {
    private val adapter by lazy {
        WordBookAdapter(Database.books) { wordBook ->
            // 点击单词本的处理
            openWordBook(wordBook)
        }
    }

    private val rvBook by lazy { requireView().findViewById<RecyclerView>(R.id.rv_book) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupTabs()
        setupSearch()
    }

    private fun setupRecyclerView() {
        // 设置网格布局的间距
        // val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        rvBook.addItemDecoration(GridSpacingItemDecoration(2, 2/*spacing*/, true))
        rvBook.adapter = adapter
    }

    private fun setupTabs() {
        view?.findViewById<TabLayout>(R.id.tabLayout)?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> adapter.filterByCategory("全部")
                    1 -> adapter.filterByCategory("考试词汇")
                    2 -> adapter.filterByCategory("生活词汇")
                    3 -> adapter.filterByCategory("专业词汇")
                    4 -> {
                        // 我的收藏 - 这里可以过滤出用户收藏的单词本
                        val favoriteBooks = Database.books.filter { it.id == "1" || it.id == "5" }
                        adapter.updateData(favoriteBooks)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        val etSearch = view?.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.et_search) ?: return
        view?.findViewById<View>(R.id.iv_search)?.setOnClickListener {
            etSearch.requestFocus()
            // 显示键盘等操作
        }
        view?.findViewById<View>(R.id.tv_filter)?.setOnClickListener {
            showFilterDialog()
        }
        // 搜索功能可以后续实现
    }

    private fun openWordBook(wordBook: WordBook) {
        Toast.makeText(requireContext(), "打开单词本: ${wordBook.title}", Toast.LENGTH_SHORT).show()
        // 跳转到单词本详情页面
        // startActivity(Intent(this, WordBookDetailActivity::class.java).apply {
        //     putExtra("word_book_id", wordBook.id)
        // })
    }

    private fun showFilterDialog() {
        // 实现筛选对话框
        Toast.makeText(requireContext(), "打开筛选", Toast.LENGTH_SHORT).show()
    }
}