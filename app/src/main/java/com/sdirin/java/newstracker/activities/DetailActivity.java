package com.sdirin.java.newstracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;

public class DetailActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        WebView webView = (WebView) findViewById(R.id.webView);

        Intent i = getIntent();

        if (i.hasExtra("EXTRA_URL")){
            webView.loadUrl(getIntent().getStringExtra("EXTRA_URL"));
        } else {
            Toast.makeText(this, "No URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
