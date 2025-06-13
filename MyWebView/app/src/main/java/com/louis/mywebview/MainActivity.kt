package com.louis.mywebview

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_FILE_CHOOSER = 1
    }

    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        initWebView()
    }

    private fun initWebView() {
        webView?.let { webView ->
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)  //启用硬件加速
//            webView.webViewClient = WebViewClient() // 设置 WebViewClient，实现网页在 WebView 内加载
//        WebView.setWebContentsDebuggingEnabled(true) //Chrome DevTools 调试 WebView
            // 添加JavaScript接口
//            webView.addJavascriptInterface(WebAppInterface(), "Android")
            webView.setWebViewClient(object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    view?.loadUrl(url) // 当前WebView加载链接，而不是启动浏览器
//                    return super.shouldOverrideUrlLoading(view, url)
                    return true // 拦截外部浏览器打开
                }
//                override fun shouldOverrideUrlLoading(
//                    view: WebView?,
//                    request: WebResourceRequest?
//                ): Boolean {
//                    return super.shouldOverrideUrlLoading(view, request)
//                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
//                    super.onReceivedSslError(view, handler, error)
                    handler?.proceed() //忽略 SSL 错误，生产环境应验证证书！
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
//                    webView.loadUrl("file:///android_asset/error.html");  // 加载本地错误页面
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // 页面加载完成
//                    progressBar.setVisibility(ProgressBar.GONE);
                }
            })

            webView.setWebChromeClient(object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)

                    progressBar?.setProgress(newProgress) // 需在布局中添加ProgressBar
                    if (newProgress == 100) {
                        progressBar?.setVisibility(View.GONE);
                    } else {
                        progressBar?.setVisibility(ProgressBar.VISIBLE);
                    }
                }

                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    params: FileChooserParams
                ): Boolean {
                    //启动系统文件选择器
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)
                    return true
                }


            })

            webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent) //用系统浏览器下载
            }

        }

        webView?.settings?.let { settings ->
            settings.javaScriptEnabled = true // 启用 JavaScript
            settings.setDomStorageEnabled(true);  // 启用 DOM 存储

//            settings.setMixedContentMode(WebViewClient.MIXED_CONTENT_ALWAYS_ALLOW)
//            settings.setAllowFileAccess(false)

//            settings.setLoadWithOverviewMode(true) // 适应屏幕
//            settings.setUseWideViewPort(true)      // 启用viewport

//            settings.setLoadsImagesAutomatically(true) //单独设置图片加载
//            settings.setSupportMultipleWindows(true) //多窗口支持

//            settings.setSupportZoom(true)  // 启用缩放
//            settings.setBuiltInZoomControls(true) // 显示缩放控件
//            settings.setDisplayZoomControls(false)

            //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK) // 优先缓存
//            settings.setCacheMode(WebSettings.LOAD_DEFAULT) // 普通模式
//            settings.setCacheMode(WebSettings.LOAD_NO_CACHE)  // 不缓存

//            settings.setAppCachePath(cacheDir.absolutePath)
//            settings.setAppCacheMaxSize(10 * 1024 * 1024) // 10MB 缓存上限
//            settings.setBlockNetworkImage(true) //按需加载图片
        }

        webView?.loadUrl("https://www.baidu.com")
    }

    // JavaScript接口类
//    class WebAppInterface {
//        @JavascriptInterface
//        fun showToast(message: String?) {
//            Toast.makeText(this@Mai, message, Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView?.canGoBack() == true) {
                webView?.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存WebView状态
        webView?.saveState(outState)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 恢复WebView状态
        webView?.restoreState(savedInstanceState)
    }

    private fun release() {
        webView?.let { webView ->
            (webView.parent as ViewGroup).removeView(webView)
            webView.clearCache(true) //清除缓存
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        }
        webView = null
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
        webView?.pauseTimers()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
        webView?.resumeTimers()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }
}