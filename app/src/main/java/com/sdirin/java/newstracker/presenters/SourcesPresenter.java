package com.sdirin.java.newstracker.presenters;

import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.database.DatabaseHandler;
import com.sdirin.java.newstracker.data.model.SourcesResponse;
import com.sdirin.java.newstracker.data.model.parse.SourcesParser;
import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.view.SourcesScreen;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 07.02.2018.
 */

public class SourcesPresenter {

    SourcesScreen screen;
    private NewsService mService;

    public SourcesResponse sourcesResponse;
    public SelectedSources selectedSources;
    private DatabaseHandler db;

    //testing network support
    private CountingIdlingResource idlingResource;
    private int incrementCalls = 0;

    public SourcesPresenter(SourcesScreen screen) {
        mService = new ServiceProvider().getService();
        this.screen = screen;
        this.db = screen.getDb();
        selectedSources = screen.getSelectedSources();
    }

    public void onResume(){
        loadFromDB();
        loadFromNetwork();
    }

    private void loadFromDB() {
        sourcesResponse = new SourcesResponse();
        sourcesResponse.setMessage("ok");
//        screen.logD("loaded DB");
        sourcesResponse.setSources(db.getAllSources());
        screen.logD("loadFromDB: sources count = "+sourcesResponse.getSources().size());
        screen.setSourcesResponse(sourcesResponse);
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
                    sourcesResponse.combineWith(sourcesResponseNetwork);
                    safeToDb(sourcesResponse);
                    loadFromDB();
                } else {
                    int statusCode = response.code();
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

    private void safeToDb(SourcesResponse sourcesResponseNetwork) {
        for (int i=0; i<sourcesResponseNetwork.getSources().size(); i++){
            db.addSource(sourcesResponseNetwork.getSources().get(i));
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
}
