package com.sdirin.java.newstracker.view.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;

/**
 * Created by SDirin on 05-Feb-18.
 */

public class NavigationDrawer {

    final NavigationView mDrawerList;
    final DrawerLayout mDrawerLayout;
    final Context context;

    public NavigationDrawer(NavigationView drawerList, DrawerLayout drawerLayout, Context context){
        mDrawerList = drawerList;
        mDrawerLayout = drawerLayout;
        this.context = context;
        mDrawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawer(mDrawerList);
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        Toast.makeText(NavigationDrawer.this.context, "Home pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.settings_menu:
                        Toast.makeText(NavigationDrawer.this.context, "Settings pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void opeDrawer(){
        mDrawerLayout.openDrawer(mDrawerList);
    }


}
