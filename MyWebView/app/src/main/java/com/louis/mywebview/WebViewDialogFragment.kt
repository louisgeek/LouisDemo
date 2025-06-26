package com.louis.mywebview

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import com.louisgeek.library.tool.ScreenTool


class WebViewDialogFragment : DialogFragment() {
    companion object {
        private const val TAG = "WebViewDialogFragment"
        private const val REQUEST_CODE_FILE_CHOOSER = 1
    }

    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        progressBar = view.findViewById(R.id.progressBar)
        //vProgressBar.setVisibility(View.VISIBLE);

        webView?.setBackgroundColorCompat(Color.TRANSPARENT)

        val width = (ScreenTool.screenWidth * 0.9f).toInt()
        val height = (ScreenTool.screenHeight * 0.9f).toInt()
        requireDialog().window?.setLayout(width, height)

        initWebView()

        webView?.loadUrl("https://www.baidu.com")
//        webView?.postUrl(url,"".toByteArray())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存WebView状态
        webView?.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.run {
            // 恢复WebView状态
            webView?.restoreState(savedInstanceState)
        }
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

    private fun initWebView() {
        webView?.let { webView ->
//            webView.webViewClient = WebViewClient() // 设置 WebViewClient，实现网页在 WebView 内加载
//        WebView.setWebContentsDebuggingEnabled(true) //Chrome DevTools 调试 WebView

            webView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
//                    return super.shouldOverrideUrlLoading(view, url)
                    if (url.startsWith("http") || url.startsWith("https")) {
                        view?.loadUrl(url) //在当前 WebView 加载链接（而不是启动浏览器）
                    }
                    return true //拦截外部浏览器打开
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
//                    webView.loadUrl("file:///android_asset/error.html"); //加载本地错误页面
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
                    progressBar?.setProgress(newProgress)
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
                //用系统浏览器下载
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }

        }

        webView?.settings?.let { settings ->
            settings.javaScriptEnabled = true // 启用 JavaScript
            settings.setDomStorageEnabled(true)  //启用 DOM 存储（比如跨页面数据共享，允许网页通过 JS 在客户端存储键值对数据）
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW) //混合内容模式（比如允许 HTTPS 页面加载 HTTP 资源）
//            settings.setAllowFileAccess(false)
//            settings.allowUniversalAccessFromFileURLs = true

            settings.setLoadWithOverviewMode(true) // 适应屏幕
            settings.setUseWideViewPort(true)      // 启用viewport

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

    }

    private fun reloadUrl() {
        webView?.clearHistory()
        webView?.clearCache(true)
        webView?.reload()
    }

    private fun goBack(): Boolean {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
            return true
        }
        return false
    }

    private fun release() {
        webView?.let { webView ->
            (webView.parent as ViewGroup).removeView(webView)
            webView.stopLoading()
            webView.clearCache(true) //清除缓存
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        }
        webView = null
    }
}