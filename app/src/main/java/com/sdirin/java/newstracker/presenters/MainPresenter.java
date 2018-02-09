package com.sdirin.java.newstracker.presenters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.sdirin.java.newstracker.data.NewsProvider;
import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.model.Article;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.model.parse.NewsParser;
import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.view.MainScreen;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_AUTHOR;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_DESCRIPTION;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_ID;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_IS_DELETED;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_IS_READ;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_NAME;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_PUBLISHED_AT;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_SOURCE_ID;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_TITLE;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_URL;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_URL_TO_IMAGE;


/**
 * Created by SDirin on 06-Jan-18.
 */

public class MainPresenter {

    private NewsService mService;
    private MainScreen screen;

    //testing network support
    private CountingIdlingResource idlingResource;
    private int incrementCalls = 0;

    ContentResolver cr;

    public MainPresenter(MainScreen screen){
        mService = new ServiceProvider().getService();
        this.screen = screen;
        this.cr = screen.getContentResolver();
    }

    public void onResume(){
    }

    public void onStart(){
        screen.askPermition();
        loadFromNetwork();
    }

    public void loadFromNetwork(){
        if (!screen.isInternetAvailable()){
            return;
        }
        incrementIdlingResouce();
        SelectedSources selected = screen.getSelectedSources();
        String sources = selected.getSelectedSources();
        if (sources.length() == 0){
            sources = "polygon";
        }
        mService.getNews(sources).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    //screen.logD("loaded network");
                    NewsResponse newsResponseNetwork;
                    try {
                        newsResponseNetwork = NewsParser.fromJson(response.body());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        screen.logD("Error loading news");
                        return;
                    }
                    screen.logD("fromNetweork: news count = "+newsResponseNetwork.getArticles().size());
                    safeToDb(newsResponseNetwork);
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
            Article article = response.getArticles().get(i);

            //insert source
            ContentValues values = new ContentValues();
            values.put(KEY_SOURCE_ID, article.getSource().getId());
            values.put(KEY_NAME, article.getSource().getName());

            cr.insert(NewsProvider.SOURCES_URI, values);

            //insert article
            values = new ContentValues();
            values.put(KEY_SOURCE_ID, article.getSource().getId());
            values.put(KEY_AUTHOR, article.getAuthor());
            values.put(KEY_TITLE, article.getTitle());
            values.put(KEY_DESCRIPTION, article.getDescription());
            values.put(KEY_URL, article.getUrl());
            values.put(KEY_URL_TO_IMAGE, article.getUrlToImage());
            values.put(KEY_PUBLISHED_AT, article.getPublishedAtFullString());
            values.put(KEY_IS_DELETED, article.isDeleted()?1:0);
            values.put(KEY_IS_READ, article.isRead()?1:0);

            cr.insert(NewsProvider.ARTICLES_URI, values);
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

    public void removeArticle(int dbId) {
        ContentValues values = new ContentValues();
        values.put(KEY_IS_DELETED, 1);
        cr.update(NewsProvider.ARTICLES_URI, values, KEY_ID + " = ?",
                new String[]{Integer.toString(dbId)});
    }


    public void setArticleRead(Article article) {
        ContentValues values = new ContentValues();
        values.put(KEY_IS_READ, 1);
        cr.update(NewsProvider.ARTICLES_URI, values, KEY_ID + " = ?",
                new String[]{Integer.toString(article.getDbId())});
    }

}
