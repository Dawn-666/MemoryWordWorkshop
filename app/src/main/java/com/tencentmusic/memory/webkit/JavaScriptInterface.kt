package com.tencentmusic.memory.webkit

interface JavaScriptInterface {
    fun speak(text: String)
    fun showInputDialog(callback: String)
    fun sendToApp(data: String)
    fun getDeviceInfo(): String
}