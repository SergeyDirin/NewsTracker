package com.sdirin.java.newstracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.utils.Const;

public class DetailActivity extends BasicActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new LocalWebViewClient());
        Intent i = getIntent();

        if (i.hasExtra("EXTRA_URL")){
            String url = getIntent().getStringExtra("EXTRA_URL");
            Log.d(Const.TAG, url);
            webView.loadUrl(url);
        } else {
            Toast.makeText(this, "No URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class LocalWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
