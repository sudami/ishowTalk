package com.example.ishow.UIActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.R;

/**
 * Created by MRME on 2016-04-22.
 */
public class BannerActivity extends AppBaseCompatActivity {
    private View view;
    private WebView webview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //s;
        super.onCreate(savedInstanceState);
        view =getView(R.layout.activity_banner);
        webview = (WebView)view. findViewById(R.id.webview_ishow);
        setContentView(view);
        setAppBaseCompactActivity(view);
        setToolbar(true,getString(R.string.banner_flow));
        setWebView();
    }

    private void setWebView() {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setBuiltInZoomControls(false);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //webview.getSettings().setDefaultFontSize(14);
        webview.getSettings().setUseWideViewPort(true);
        //webview.setInitialScale(50);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        String load_url = getIntent().getStringExtra("center_image_link");
        DisplayMetrics dm = getResources().getDisplayMetrics();

        int scale = dm.densityDpi;

        if (scale == 240) { //

            webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        } else if (scale == 160) {

            webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);

        } else {

            webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        }

        webview.loadUrl(load_url);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                addContentView(webview);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
       /* webview.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });*/

    }
}
