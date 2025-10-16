package com.tencentmusic.memory.webkit

import android.annotation.SuppressLint
import android.webkit.WebSettings

fun WebSettings.init() {
    // 基础设置
    @SuppressLint("SetJavaScriptEnabled")
    javaScriptEnabled = true
    allowFileAccess = true
    allowContentAccess = true
    // 处理 Android 5.0+ 混合内容
    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    // 启用DOM存储API
    domStorageEnabled = true
    // 允许WebView使用数据库
    @Suppress("DEPRECATION")
    databaseEnabled = true
    // 设置缓存模式
    cacheMode = WebSettings.LOAD_DEFAULT
    // 允许跨域访问
    @Suppress("DEPRECATION")
    allowFileAccessFromFileURLs = true
    @Suppress("DEPRECATION")
    allowUniversalAccessFromFileURLs = true
}