package com.sdirin.java.newstracker.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.adapters.SourcesAdapter;
import com.sdirin.java.newstracker.data.NewsProvider;
import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.model.SourcesResponse;
import com.sdirin.java.newstracker.presenters.SourcesPresenter;
import com.sdirin.java.newstracker.view.SourcesScreen;

public class SourcesActivity extends BasicActivity implements SourcesScreen, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "NewsApp";
    private static final int PERMISSIONS_REQUEST_INTERNET = 1;

    SourcesResponse sourcesResponse;
    SourcesPresenter presenter;

    SourcesAdapter adapter;

    RecyclerView mRecycleView;
    RecyclerView.LayoutManager layoutManager;

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private static final int LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sources);

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        presenter = new SourcesPresenter(this);
    }

    @Override
    public void displayList() {
        if (sourcesResponse == null) {
            return;
        }
        mRecycleView = findViewById(R.id.sources_list);
        if (adapter == null){
            adapter = new SourcesAdapter(presenter);
            layoutManager = new LinearLayoutManager(this);
            mRecycleView.setLayoutManager(layoutManager);
            mRecycleView.setItemAnimator(new DefaultItemAnimator());
            mRecycleView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setSourcesResponse(SourcesResponse sourcesResponse) {
        if (this.sourcesResponse == null){
            this.sourcesResponse = new SourcesResponse();
        }
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public SelectedSources getSelectedSources() {
        return new SelectedSources(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.onResume();
        displayList();
    }

//    @Override
//    public DatabaseHandler getDb() {
//        return new DatabaseHandler(this);
//    }

    public void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri loaderUri = NewsProvider.SOURCES_URI;

        return new CursorLoader(this, loaderUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        mRecycleView = findViewById(R.id.sources_list);
        if (adapter == null) {
            adapter = new SourcesAdapter(presenter);
            layoutManager = new LinearLayoutManager(this);
            mRecycleView.setLayoutManager(layoutManager);
            mRecycleView.setItemAnimator(new DefaultItemAnimator());
            mRecycleView.setAdapter(adapter);
        }

        adapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {adapter.swapCursor(null);}
}
