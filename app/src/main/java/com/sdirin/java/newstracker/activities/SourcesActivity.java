package com.sdirin.java.newstracker.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.sdirin.java.newstracker.data.network.InternetCommon;
import com.sdirin.java.newstracker.presenters.SourcesPresenter;
import com.sdirin.java.newstracker.utils.Const;
import com.sdirin.java.newstracker.view.SourcesScreen;

public class SourcesActivity extends BasicActivity implements SourcesScreen, LoaderManager.LoaderCallbacks<Cursor> {

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
            InternetCommon.startInternetLoader(this);
        }
    }

    public void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    public void logD(String message){
        Log.d(Const.TAG,message);
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
