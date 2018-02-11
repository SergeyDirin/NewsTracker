package com.sdirin.java.newstracker.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.sdirin.java.newstracker.CleanUpService;
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
    private static final String SCROLL_STATE = "savedScrol";
    private static final int LOADER_ID = 1;
    MainPresenter presenter;

    MainCursorAdapter adapter;

    RecyclerView mRecycleView;
    RecyclerView.LayoutManager layoutManager;


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
            case R.id.backup: {
                if (isPermitionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    backupDb();
                } else {
                    askPermition(Manifest.permission.WRITE_EXTERNAL_STORAGE, BasicActivity.PERMISSIONS_REQUEST_READ_STORAGE);
                }
                return true;
            }
            case R.id.cleanup: {
                Intent intent = new Intent(this, CleanUpService.class);
                startService(intent);
//                Toast.makeText(this, "Cleanup Started", Toast.LENGTH_SHORT).show();
            }
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
        if (!isPermitionGranted(Manifest.permission.INTERNET)){
            askPermition(Manifest.permission.INTERNET,BasicActivity.PERMISSIONS_REQUEST_INTERNET);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BasicActivity.PERMISSIONS_REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                presenter.loadFromNetwork();
            } else {

                Toast.makeText(this, "Internet premision denied", Toast.LENGTH_SHORT).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        } if (requestCode == BasicActivity.PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                backupDb();
            } else {
                logD("External write permition denied");
            }
        } else {
            // Ignore all other requests.
        }

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
}
