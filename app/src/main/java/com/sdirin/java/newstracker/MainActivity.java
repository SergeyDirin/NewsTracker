package com.sdirin.java.newstracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.sdirin.java.newstracker.adapters.MainAdapter;
import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.database.DatabaseHandler;
import com.sdirin.java.newstracker.presenters.MainPresenter;
import com.sdirin.java.newstracker.view.MainScreen;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements MainScreen {

    private static final String TAG = "NewsApp";
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.onResume();
        displayList();
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
    public boolean isInternetAvailable() {
        if (getConnectivityStatus() == TYPE_NOT_CONNECTED){
            Toast.makeText(this, R.string.unavailable_network, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getConnectivityStatus() == TYPE_MOBILE) {
            Toast.makeText(this, R.string.wifi_not_available, Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            InetAddress ipAddr = InetAddress.getByName(ServiceProvider.BASE_HOST);
            if(ipAddr.equals("")) {
                Toast.makeText(this, R.string.no_internet_access, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            Toast.makeText(this, R.string.no_internet_access, Toast.LENGTH_SHORT).show();
            return false;
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
