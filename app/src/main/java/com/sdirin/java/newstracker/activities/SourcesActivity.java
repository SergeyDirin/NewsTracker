package com.sdirin.java.newstracker.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
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
import com.sdirin.java.newstracker.data.network.InternetLoader;
import com.sdirin.java.newstracker.data.network.InternetLoaderService;
import com.sdirin.java.newstracker.presenters.SourcesPresenter;
import com.sdirin.java.newstracker.view.SourcesScreen;

public class SourcesActivity extends BasicActivity implements SourcesScreen, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "NewsApp";

    SourcesResponse sourcesResponse;
    SourcesPresenter presenter;

    SourcesAdapter adapter;

    RecyclerView mRecycleView;
    RecyclerView.LayoutManager layoutManager;

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
    protected void onStart() {
        super.onStart();
        presenter.isChanged = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter.isChanged){
            startInternetLoader();
        }
    }

    private void startInternetLoader() {
        Log.d(MainActivity.TAG, "starting loader");
        if (isInternetAvailable()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                PersistableBundle extra = null;
                extra = new PersistableBundle();
                extra.putString(MainActivity.SELECTED_SOURCES,new SelectedSources(this).getSelectedSources());
                jobScheduler.schedule(new JobInfo.Builder(
                        MainActivity.INTERNET_LOADER_ID,
                        new ComponentName(this, InternetLoader.class)
                ).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPeriodic(60 * 60 * 1000) //every hour
                        .setPersisted(true)
                        .setExtras(extra)
                        .build());
            } else {
                Intent service = new Intent(this, InternetLoaderService.class);
                startService(service);
            }
        } else {
            Log.d(MainActivity.TAG, "Internet is unavailable");
        }
    }

    public void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    public void logD(String message){
        Log.d(TAG,message);
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
