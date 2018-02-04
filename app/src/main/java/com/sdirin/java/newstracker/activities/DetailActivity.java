package com.sdirin.java.newstracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final NavigationView mDrawerList = (NavigationView) findViewById(R.id.navigation);
        mDrawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(mDrawerList);
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        Toast.makeText(DetailActivity.this, "Home pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.settings_menu:
                        Toast.makeText(DetailActivity.this, "Settings pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

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
                ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(findViewById(R.id.navigation));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
