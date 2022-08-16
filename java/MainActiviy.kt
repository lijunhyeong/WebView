import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()

    }

    override fun onBackPressed() {
        // 웹뷰 history에 쌓여있는 뷰가 있으면 goBack
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply {
            // 외부 webView로 가지 않고 밑에 설정한 Web이 나온다.
            webViewClient = WebViewClient()
            //
            webChromeClient = WebChromeClient()
            // 자바스크립트를 웹뷰에서 사용하겠다.
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_RUL)
        }
    }

    private fun bindViews() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_RUL)
        }

        addressBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()
                if (URLUtil.isNetworkUrl(loadingUrl)){
                    webView.loadUrl(loadingUrl)
                }else{
                    webView.loadUrl("http://$loadingUrl")
                }
            }

            return@setOnEditorActionListener false
        }

        // WebView 뒤로 가기
        goBackButton.setOnClickListener {
            webView.goBack()
        }
        // WebView 앞으로 가기
        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        // webView refresh
        refreshLayout.setOnRefreshListener { webView.reload() }
    }

    // inner 상위에 있는 클래스에 접근 가능
    inner class WebViewClient : android.webkit.WebViewClient() {
        // 페이지가 시작할 때
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()
        }

        // 페이지 로딩 완료 됐을 때, refreshLayout 로딩후 없애주기 위해
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            // 뒤로 갈 페이지가 없을 시 뒤로가기 버튼 비활성화
            goBackButton.isEnabled = webView.canGoBack()
            //
            goForwardButton.isEnabled = webView.canGoForward()
            // EditText에 url 전달
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
        }
    }

    companion object {
        private const val DEFAULT_RUL = "http://www.google.com"
    }

}
