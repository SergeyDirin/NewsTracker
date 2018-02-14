package com.sdirin.java.newstracker.presenters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.sdirin.java.newstracker.data.NewsProvider;
import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.view.SourcesScreen;

import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_IS_DELETED;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_SOURCE_ID;

/**
 * Created by User on 07.02.2018.
 */

public class SourcesPresenter {

    SourcesScreen screen;
    private NewsService mService;

    public SelectedSources selectedSources;

    //testing network support
    private CountingIdlingResource idlingResource;
    private int incrementCalls = 0;

    public boolean isChanged = false;

    ContentResolver cr;

    public SourcesPresenter(SourcesScreen screen) {
        mService = new ServiceProvider().getService();
        this.screen = screen;
        //this.db = screen.getDb();
        selectedSources = screen.getSelectedSources();
        this.cr = screen.getContentResolver();
    }

    public void removeSource(int dbId) {
        ContentValues values = new ContentValues();
        values.put(KEY_IS_DELETED, 1);
        cr.update(NewsProvider.SOURCES_URI, values, KEY_SOURCE_ID + " = ?",
                new String[]{Integer.toString(dbId)});
    }
}
