package com.sdirin.java.newstracker.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.adapters.MainAdapter;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.database.DatabaseHandler;
import com.sdirin.java.newstracker.presenters.MainPresenter;
import com.sdirin.java.newstracker.view.MainScreen;

public class MainActivity extends AppCompatActivity implements MainScreen {

    private static final String TAG = "NewsApp";
    private static final int PERMISSIONS_REQUEST_INTERNET = 1;
    NewsResponse newsResponse;
    MainPresenter presenter;

    MainAdapter adapter;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
                @Override
                public void onNetworkActive() {

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.onResume();
        displayList();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        presenter.onStart();
        askPermition();
    }

    public MainPresenter getPresenter(){
        return presenter;
    }

    public void setNewsResponse(NewsResponse newsResponse){
        if (this.newsResponse == null){
            this.newsResponse = new NewsResponse();
        }
        this.newsResponse.combineWith(newsResponse);
    }

    public void displayList() {
        if (newsResponse == null) {
            return;
        }
        RecyclerView list = findViewById(R.id.news_list);
        if (adapter == null){
            adapter = new MainAdapter(newsResponse);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            list.setLayoutManager(layoutManager);
            list.setItemAnimator(new DefaultItemAnimator());
            list.setAdapter(adapter);
        } else {
            list.invalidate();
        }

    }

    public void logD(String message){
        Log.d(TAG,message);
    }

    @Override
    public boolean isPermitionGranted(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void askPermition(){
        if (!isPermitionGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST_INTERNET);
        }
    }
    @Override
    public boolean isInternetAvailable() {

        if (getConnectivityStatus() == TYPE_NOT_CONNECTED){
            Toast.makeText(this, R.string.unavailable_network, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getConnectivityStatus() == TYPE_MOBILE) {
            Toast.makeText(this, R.string.wifi_not_available, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Premisson Result");

        if (requestCode == PERMISSIONS_REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                presenter.onPermitionGranted();
            } else {

                Toast.makeText(this, "Internet premision denied", Toast.LENGTH_SHORT).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        } else {

            // Ignore all other requests.
        }

    }

    public int getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    @Override
    public DatabaseHandler getDb() {
        return new DatabaseHandler(this);
    }

    public void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    public void setCountingIdlingResource(CountingIdlingResource countingIdlingResource) {
        presenter.setCountingIdlingResource(countingIdlingResource);
    }
}
