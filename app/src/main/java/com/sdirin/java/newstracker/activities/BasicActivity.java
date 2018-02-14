package com.sdirin.java.newstracker.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.view.components.NavigationDrawer;

/**
 * Created by User on 07.02.2018.
 */

public class BasicActivity extends AppCompatActivity {


    public static final int PERMISSIONS_REQUEST_INTERNET = 1;
    public static final int PERMISSIONS_REQUEST_READ_STORAGE = 2;

    NavigationDrawer navigationDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        navigationDrawer = new NavigationDrawer(
                (NavigationView) findViewById(R.id.navigation),
                (DrawerLayout) findViewById(R.id.drawer_layout),
                this
        );
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

    public boolean isPermitionGranted(String permission){
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
    public void askPermition(String permission, int resultCode){
        if (!isPermitionGranted(Manifest.permission.INTERNET)) {
            ActivityCompat.requestPermissions(this,
                    new String[] {permission}, resultCode);
        }
    }

}
