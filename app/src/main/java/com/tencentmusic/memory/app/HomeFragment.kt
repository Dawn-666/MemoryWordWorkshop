package com.tencentmusic.memory.app

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.tencentmusic.memory.R
import com.tencentmusic.memory.viewmodel.PlayerViewModel.Companion.playerViewModels
import com.tencentmusic.memory.webkit.JavaScriptInterface
import com.tencentmusic.memory.webkit.init
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val player by playerViewModels()
    private val webView by lazy { requireView().findViewById<WebView>(R.id.webView) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView.apply {
            settings.init()
            // 添加JavaScript接口
            addJavascriptInterface(object : JavaScriptInterface {
                @JavascriptInterface
                override fun speak(text: String) {
                    (context as? Activity)?.runOnUiThread {
                        player.speak(text, true)
                    }
                }

                @JavascriptInterface
                override fun showInputDialog(callback: String) {
                    callback(callback)
                }

                @JavascriptInterface
                override fun sendToApp(data: String) {
                    // 处理从Web接收的数据
                    Log.d("WebApp", "Received from web: $data")
                }

                @JavascriptInterface
                override fun getDeviceInfo(): String {
                    // 返回设备信息给Web
                    return "{\"platform\":\"Android\",\"version\":\"${Build.VERSION.RELEASE}\"}"
                }

                private fun callback(callback: String, data: JSONObject? = null) {
                    activity?.runOnUiThread {
                        evaluateJavascript("javascript:$callback('$data')", null)
                    }
                }
            }, "Android")
            // 设置WebViewClient处理页面加载
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    // 保持所有链接在WebView内打开
                    return false
                }

                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    val url = request.url.toString()
                    Log.d("WebApp", "拦截请求: $url")
                    // 处理CSS文件
                    if (url.endsWith(".css")) {
                        try {
                            val cssStream = activity?.assets?.open(url.replace("file:///android_asset/", ""))
                            return WebResourceResponse("text/css", "UTF-8", cssStream)
                        } catch (e: IOException) {
                            Log.e("WebApp", "加载CSS失败: ${e.message}")
                        }
                    }
                    // 处理JS文件
                    if (url.endsWith(".js")) {
                        try {
                            val jsStream = activity?.assets?.open(url.replace("file:///android_asset/", ""))
                            return WebResourceResponse("application/javascript", "UTF-8", jsStream)
                        } catch (e: IOException) {
                            Log.e("WebApp", "加载JS失败: ${e.message}")
                        }
                    }
                    request.url.let { url ->
                        if (url.scheme == "file" && url.host != "android_asset") {
                            val path = ("${url.host}${url.path}").removePrefix("/")
                            try {
                                return WebResourceResponse(null, "UTF-8", activity?.assets?.open(if (path.startsWith("h5")) path else "h5/$path"))
                            } catch (e: Exception) {
                                Log.e("WebApp", Log.getStackTraceString(e))
                            }
                        }
                    }
                    return null
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    Log.d("WebApp", "页面加载完成: $url")
                }
            }
            webChromeClient = WebChromeClient()
        }
    }

    override fun onResume() {
        super.onResume()
        if (webView.originalUrl?.startsWith(originUrl) != true) {
            webView.loadUrl(originUrl)
        }
    }

    companion object {
        private const val URL = "file:///android_asset/h5/index.html"

        private val originUrl
            get() = testUrl ?: URL

        var testUrl: String? = null
    }
}