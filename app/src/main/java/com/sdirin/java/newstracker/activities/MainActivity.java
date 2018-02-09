package com.sdirin.java.newstracker.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.adapters.MainCursorAdapter;
import com.sdirin.java.newstracker.data.NewsProvider;
import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.presenters.MainPresenter;
import com.sdirin.java.newstracker.view.MainScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static com.sdirin.java.newstracker.data.database.DatabaseHandler.DATABASE_NAME;


public class MainActivity extends BasicActivity implements MainScreen, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "NewsApp";
    private static final int PERMISSIONS_REQUEST_INTERNET = 1;
    private static final String SCROLL_STATE = "savedScrol";
    private static final int LOADER_ID = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 2;
    MainPresenter presenter;

    MainCursorAdapter adapter;

    RecyclerView mRecycleView;
    RecyclerView.LayoutManager layoutManager;


    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    Parcelable state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
                @Override
                public void onNetworkActive() {
                    presenter.loadFromNetwork();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                checkPermissionReadStorage(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //state = layoutManager.onSaveInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.onResume();
//        displayList();
    }

    @Override
    public SelectedSources getSelectedSources() {
        return new SelectedSources(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
        askPermition();
    }

    public MainPresenter getPresenter(){
        return presenter;
    }

    public void setNewsResponse(NewsResponse newsResponse){
//        if (this.newsResponse == null){
//            this.newsResponse = new NewsResponse();
//        }
//        if (adapter != null){
//            adapter.notifyDataSetChanged();
//        }
    }

    public void displayList() {
//        if (newsResponse == null) {
//            return;
//        }
//        mRecycleView = findViewById(R.id.news_list);
//        if (adapter == null){
//            adapter = new MainCursorAdapter();
//            layoutManager = new LinearLayoutManager(this);
//            mRecycleView.setLayoutManager(layoutManager);
//            mRecycleView.setItemAnimator(new DefaultItemAnimator());
//            mRecycleView.setAdapter(adapter);
//            layoutManager.onRestoreInstanceState(state);
//        } else {
//            adapter.notifyDataSetChanged();
//        }

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

        if (requestCode == PERMISSIONS_REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                presenter.loadFromNetwork();
            } else {

                Toast.makeText(this, "Internet premision denied", Toast.LENGTH_SHORT).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        } if (requestCode == MY_PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                backupDb();
            } else {
                logD("External write permition denied");
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

    public void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    public void logD(String message){
        Log.d(TAG,message);
    }

    public void setCountingIdlingResource(CountingIdlingResource countingIdlingResource) {
        presenter.setCountingIdlingResource(countingIdlingResource);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SCROLL_STATE,state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_STATE)){
            state = savedInstanceState.getParcelable(SCROLL_STATE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri loaderUri = NewsProvider.ARTICLES_URI;

        return new CursorLoader(this, loaderUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor c) {
        if (adapter == null) {
            adapter = new MainCursorAdapter(presenter);
            mRecycleView = findViewById(R.id.news_list);
            layoutManager = new LinearLayoutManager(this);
            mRecycleView.setLayoutManager(layoutManager);
            mRecycleView.setItemAnimator(new DefaultItemAnimator());
            mRecycleView.setAdapter(adapter);
        }

        adapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }

    public void backupDb() {
        File sd = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp");
        File currentDB = getApplicationContext().getDatabasePath(DATABASE_NAME); //databaseName=your current application database name, for example "my_data.db"
        if (sd.canWrite()) {
            File backupDB = new File(sd, DATABASE_NAME+".sqlite"); // for example "my_data_backup.db"
            if (currentDB.exists()) {
                FileChannel src = null;
                try {
                    src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    logD("Backup failed");
                }

                logD("Backup Done");
            } else {
                logD("DB not found");
            }
        } else {
            logD("Backup permission denied");
            logD(currentDB.getAbsolutePath());
        }
    }
    public void checkPermissionReadStorage(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,      Manifest.permission.WRITE_EXTERNAL_STORAGE) !=     PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            backupDb();
        }
    }
}
