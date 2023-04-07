package com.drhowdydoo.animenews;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.drhowdydoo.animenews.databinding.ActivityWebPageBinding;

public class WebPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWebPageBinding binding = ActivityWebPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.icReader.setVisibility(View.VISIBLE);
        binding.progressIndicator.setVisibility(View.VISIBLE);
        Transition transition = new Fade();
        transition.setDuration(800);
        String url = getIntent().getStringExtra("com.drhowdydoo.url");
        Log.d("WebPage", "onCreate: " + url);
        WebView webView = binding.webView;
        webView.setInitialScale(0);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                TransitionManager.beginDelayedTransition(binding.getRoot(),transition);
                binding.icReader.setVisibility(View.GONE);
                binding.progressIndicator.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        if (url != null) {
            webView.loadUrl(url);
        }
    }
}