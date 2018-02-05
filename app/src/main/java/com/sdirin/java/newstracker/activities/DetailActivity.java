package com.sdirin.java.newstracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.view.components.NavigationDrawer;

public class DetailActivity extends AppCompatActivity {

    NavigationDrawer navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        navigationDrawer = new NavigationDrawer(
                (NavigationView) findViewById(R.id.navigation),
                (DrawerLayout) findViewById(R.id.drawer_layout),
                this
        );

        WebView webView = (WebView) findViewById(R.id.webView);

        Intent i = getIntent();

        if (i.hasExtra("EXTRA_URL")){
            webView.loadUrl(getIntent().getStringExtra("EXTRA_URL"));
        } else {
            Toast.makeText(this, "No URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.openMenu:
                navigationDrawer.opeDrawer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
