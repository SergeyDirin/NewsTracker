package com.sdirin.java.newstracker.presenters;

import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.data.parse.NewsServiceParser;
import com.sdirin.java.newstracker.database.DatabaseHandler;
import com.sdirin.java.newstracker.view.MainScreen;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by SDirin on 06-Jan-18.
 */

public class MainPresenter {

    private NewsResponse newsResponse;
    private NewsService mService;
    private DatabaseHandler db;

    private MainScreen screen;

    //testing network support
    private CountingIdlingResource idlingResource;
    private int incrementCalls = 0;

    public MainPresenter(MainScreen screen){
        mService = new ServiceProvider().getService();
        this.screen = screen;
        this.db = screen.getDb();
    }

    private void loadFromDB() {
        newsResponse = new NewsResponse();
        newsResponse.setMessage("ok");
        screen.logD("loaded DB");
        newsResponse.setArticles(db.getAllArticles());
        screen.setNewsResponse(newsResponse);
    }

    public void onResume(){
        loadFromDB();
        loadFromNetwork();
    }

    private void loadFromNetwork(){
        incrementIdlingResouce();
        mService.getNews().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    screen.logD("loaded network");
                    NewsResponse newsResponseNetwork;
                    try {
                        newsResponseNetwork = NewsServiceParser.fromJson(response.body());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        screen.logD("Error loading news");
                        return;
                    }
                    if (newsResponse == null){
                        newsResponse = newsResponseNetwork;
                    } else {
                        newsResponse.combineWith(newsResponseNetwork );
                    }
                    safeToDb(newsResponse);
                    screen.setNewsResponse(newsResponse);
                    screen.displayList();
                } else {
                    int statusCode = response.code();
                    screen.logD("onResponse: status code = "+statusCode);
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

    private void safeToDb(NewsResponse response) {
        for (int i=0; i<response.getArticles().size(); i++){
            db.addArticle(response.getArticles().get(i));
        }
    }

    //testing network support
    public void setCountingIdlingResource(CountingIdlingResource countingIdlingResource) {
        this.idlingResource = countingIdlingResource;
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
