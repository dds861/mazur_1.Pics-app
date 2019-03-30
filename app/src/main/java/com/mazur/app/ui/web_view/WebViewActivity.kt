package com.mazur.app.ui.web_view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import com.mazur.app.EXTRA_TASK_URL
import com.mazur.app.R
import com.mazur.app._core.BaseActivity
import im.delight.android.webview.AdvancedWebView
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.content_progress_bar.*


/**
 * Created by Andriy Deputat email(andriy.deputat@gmail.com) on 3/13/19.
 */
class WebViewActivity : BaseActivity(), AdvancedWebView.Listener {

    private lateinit var webView: AdvancedWebView
    private lateinit var progressBar: ProgressBar
    private lateinit var webSettings: WebSettings

    override fun getContentView(): Int = R.layout.activity_web_view

    override fun initUI() {
        webView = web_view
        progressBar = progress_bar
    }

    override fun setUI() {
        logEvent("web-view-screen")
        progressBar.visibility = View.VISIBLE

        configureWebView()

        webView.loadUrl(intent.getStringExtra(EXTRA_TASK_URL))


//        webSettings = webView.settings
//        webSettings.javaScriptEnabled = true
//        webView.addJavascriptInterface(
//            WebAppInterface(applicationContext),
//            "Android"
//        )

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                progressBar.visibility = View.GONE
//                Toast.makeText(applicationContext, url, Toast.LENGTH_SHORT).show()
                Log.i("autolog", "url: " + url);
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                super.shouldOverrideUrlLoading(view, request)
                Log.i("autolog", "request: " + request);

                return true
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true


    }

    override fun onBackPressed() {
        if (!webView.onBackPressed()) {
            return
        }
        super.onBackPressed()
        webView.onDestroy()
        finish()

    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webView.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webView.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onPageFinished(url: String?) {
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
    }

    override fun onExternalPageRequest(url: String?) {
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
    }
}