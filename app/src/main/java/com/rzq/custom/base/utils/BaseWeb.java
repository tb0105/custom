package com.rzq.custom.base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.rzq.custom.R;
import com.rzq.custom.base.BaseAc;

import java.io.File;

/**
 * Created by asus on 2017/7/10.
 */

public abstract class BaseWeb extends BaseAc implements View.OnClickListener {
    public MyWebView webView;
    private String APP_CACAHE_DIRNAME = "/webcache";
    protected ProgressBar pb_shellmall;
    private RelativeLayout mLayout;
    TextView tv_title;
    public ValueCallback<Uri> uploadMessage;
    public ValueCallback<Uri[]> uploadMessageAboveL;
    public final static int FILE_CHOOSER_RESULT_CODE = 10000;
    public static final int TYPE_GALLERY = 2;
    public MyWebChromeClient mywebChromeClient;

    protected abstract boolean isheadline();//是否随网页标题


    protected abstract String url();//链接地址

    protected abstract String TAG();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_webview);
        initwebview();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void initwebview() {
        mLayout = (RelativeLayout) findViewById(R.id.RL_webview);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView = new MyWebView(mActivity);
        webView.setLayoutParams(params);
        mLayout.addView(webView);
        pb_shellmall = (ProgressBar) findViewById(R.id.PB_webview);
        tv_title = findViewById(R.id.tv_title);
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();
        //设置在WebView内部是否允许访问文件，默认允许访问。
        webSettings.setAllowFileAccess(true);
        //是否允许在WebView中访问内容URL（Content Url）
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        //应用缓存API是否可用
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(APP_CACAHE_DIRNAME);
        //是否禁止从网络（通过http和https URI schemes访问的资源）下载图片资源
        webSettings.setBlockNetworkImage(false);

        webSettings.setBlockNetworkLoads(false);
        //设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        //数据库存储API是否可用
        webSettings.setDatabaseEnabled(true);
        //DOM存储API是否可用，默认false。
        webSettings.setDomStorageEnabled(true);
        //定位
        webSettings.setGeolocationEnabled(true);
        //设置WebView是否允许执行JavaScript脚本，默认false，不允许。
        webSettings.setJavaScriptEnabled(true);
        //让JavaScript自动打开窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        //WebView是否下载图片资源，
        webSettings.setLoadsImagesAutomatically(true);

        // 混合模式 ,android 5.0以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        //WebView是否支持HTML的“viewport”标签或者使用wide viewport
        webSettings.setUseWideViewPort(true);
        webView.setWebViewClient(new

                myViewClient());
        mywebChromeClient = new MyWebChromeClient();
        webView.setWebChromeClient(mywebChromeClient);
        webView.loadUrl(

                url());

        setAcceptThirdPartyCookies();
    }


    public class myViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //这个事件就是开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。

        }

        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
//            System.out.println(url);
            // 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //在页面加载结束时调用。同样道理，我们可以关闭loading 条，切换程序动作。
            pb_shellmall.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();  // 接受所有网站的证书
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            // (报告错误信息)
            Log.e(TAG(), error.toString());
            pb_shellmall.setVisibility(View.GONE);
            // TODO Auto-generated method stub
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return false;
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ib_back) {
            finish();
        }
    }

    /**
     * 设置cookie
     */
    public final void setAcceptThirdPartyCookies() {
        syncCookie(mContext, url());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {


        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
        Log.e(TAG(), "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");
        Log.e(TAG(), "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
//            FileUtil.deleteFile(String.valueOf(webviewCacheDir));
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
//            FileUtil.deleteFile(String.valueOf(appCacheDir));
        }
    }

    /**
     * Sync Cookie
     */
    private void syncCookie(Context context, String url) {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();// 移除
            cookieManager.removeAllCookie();
            if (url != null) {
                String oldCookie = cookieManager.getCookie(url);
                if (oldCookie != null) {
                }
                StringBuilder sbCookie = new StringBuilder();
                sbCookie.append(String.format("JSESSIONID=%s", "INPUT YOUR JSESSIONID STRING"));
                sbCookie.append(String.format(";domain=%s", "INPUT YOUR DOMAIN STRING"));
                sbCookie.append(String.format(";path=%s", "INPUT YOUR PATH STRING"));
                String cookieValue = sbCookie.toString();
                cookieManager.setCookie(url, cookieValue);
                CookieSyncManager.getInstance().sync();
                String newCookie = cookieManager.getCookie(url);
                if (newCookie != null) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG(), "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (webView!=null&&webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                if (webView != null) {
                    webView.clearHistory();
                    ((ViewGroup) webView.getParent()).removeView(webView);
                    webView.destroy();
                    webView = null;
                }
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
//----------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.clearHistory();
            webView.destroy();
            webView = null;
        }
        clearWebViewCache();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private class MyWebChromeClient extends WebChromeClient { //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            return true;
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (tv_title != null)
                tv_title.setText(title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb_shellmall.setProgress(newProgress);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

    }

    /**
     * webview
     */
    public class MyWebView extends WebView {
        public MyWebView(Context context) {
            super(context);
        }

        private BaseOnClick onDrawCallBack;
        private boolean isRendered = false;

        public void setOnDrawCallBack(BaseOnClick onDrawCallBack) {
            this.onDrawCallBack = onDrawCallBack;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!isRendered) {
                Log.d("ArticleWebView", "getContentHeight():" + getContentHeight());
                isRendered = getContentHeight() > 10;
                if (onDrawCallBack != null) {
                    onDrawCallBack.call(getContentHeight(), null);
                }
            }
        }
    }

}
