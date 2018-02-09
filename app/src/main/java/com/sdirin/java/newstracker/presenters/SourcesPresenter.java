package com.sdirin.java.newstracker.presenters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.sdirin.java.newstracker.data.NewsProvider;
import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.database.DatabaseHandler;
import com.sdirin.java.newstracker.data.model.Source;
import com.sdirin.java.newstracker.data.model.SourcesResponse;
import com.sdirin.java.newstracker.data.model.parse.SourcesParser;
import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.view.SourcesScreen;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    ContentResolver cr;

    public SourcesPresenter(SourcesScreen screen) {
        mService = new ServiceProvider().getService();
        this.screen = screen;
        //this.db = screen.getDb();
        selectedSources = screen.getSelectedSources();
        this.cr = screen.getContentResolver();
    }

    public void onResume(){
        loadFromNetwork();
    }

    private void loadFromNetwork() {
        if (!screen.isInternetAvailable()) {
            return;
        }
        screen.logD("Sources loadFromNetwork");
//        if (isLoadedWithPermition){
//            return;
//        }
//        isLoadedWithPermition = true;
        incrementIdlingResouce();
        mService.getSources().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    //screen.logD("loaded network");
                    SourcesResponse sourcesResponseNetwork;
                    try {
                        sourcesResponseNetwork = SourcesParser.fromJson(response.body());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        screen.logD("Error loading news");
                        return;
                    }
                    screen.logD("Sources onResponse: sources count = "+sourcesResponseNetwork.getSources().size());
                    safeToDb(sourcesResponseNetwork);
                } else {
//                    int statusCode = response.code();
                    //screen.logD("onResponse: status code = "+statusCode);
                }
                decrementIdlingResource();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                screen.showErrorMessage();
                screen.logD("onResponse: error loading from API");
                decrementIdlingResource();
            }
        });
    }

    private void safeToDb(SourcesResponse response) {
        for (int i=0; i<response.getSources().size(); i++){
            Source source = response.getSources().get(i);

            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.KEY_SOURCE_ID, source.getId());
            values.put(DatabaseHandler.KEY_NAME, source.getName());
            values.put(DatabaseHandler.KEY_DESCRIPTION_SOURCE, source.getDescription());
            values.put(DatabaseHandler.KEY_URL_SOURCE, source.getUrl());
            values.put(DatabaseHandler.KEY_CATEGORY, source.getCategory());
            values.put(DatabaseHandler.KEY_LANGUAGE, source.getLanguage());
            values.put(DatabaseHandler.KEY_COUNTRY, source.getCountry());

            cr.insert(NewsProvider.SOURCES_URI, values);
        }
    }

    private void incrementIdlingResouce(){
        if (idlingResource != null){
            idlingResource.increment();
        } else {
            incrementCalls++;
        }
    }
    private void decrementIdlingResource(){
        if (incrementCalls > 0){
            incrementCalls--;
            return;
        }
        if (idlingResource != null){

            idlingResource.decrement();
        }
    }

    public void removeSource(int dbId) {
        ContentValues values = new ContentValues();
        values.put(KEY_IS_DELETED, 1);
        cr.update(NewsProvider.SOURCES_URI, values, KEY_SOURCE_ID + " = ?",
                new String[]{Integer.toString(dbId)});
    }
}
