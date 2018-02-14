package com.sdirin.java.newstracker.data.network;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sdirin.java.newstracker.data.NewsProvider;
import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.database.DatabaseHandler;
import com.sdirin.java.newstracker.data.model.Article;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.model.Source;
import com.sdirin.java.newstracker.data.model.SourcesResponse;
import com.sdirin.java.newstracker.data.model.parse.NewsParser;
import com.sdirin.java.newstracker.data.model.parse.SourcesParser;
import com.sdirin.java.newstracker.utils.Const;
import com.sdirin.java.newstracker.utils.DateFormater;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An subclass for loading internet data for SDK < LOLYPOP
 */
public class InternetLoaderService extends IntentService {

    private static final int SERVICE_ID = 1;
    static Date lastUpdated;
    static int failInterval = 5000;
    static int maxInterval = 60*60*1000; // 1 hour
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public InternetLoaderService() { super("InternetLoaderService"); }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(getApplicationContext(), "Data Load finished", Toast.LENGTH_SHORT).show();
        Log.d(Const.TAG,"Data Load finished");
        super.onDestroy();
    }

    private void loadLastUpdated() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                SelectedSources.PREF, Context.MODE_PRIVATE);
        long prefLastUpdated = prefs.getLong(InternetLoader.PREF_LAST_UPDATED, 0);
        if (prefLastUpdated != 0) {
            lastUpdated = new Date(prefLastUpdated);
        }
    }

    private void saveLastUpdated(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                SelectedSources.PREF, Context.MODE_PRIVATE);
        prefs.edit().putLong(InternetLoader.PREF_LAST_UPDATED, lastUpdated.getTime()).apply();
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        loadLastUpdated();
        String sources = new SelectedSources(getApplicationContext()).getSelectedSources();
        if (sources.length() == 0){
            sources = "polygon";
        }
        if (lastUpdated == null){
            lastUpdated = new Date();
            new ServiceProvider().getService().getNews(sources).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful()) {
                        NewsResponse newsResponseNetwork;
                        try {
                            newsResponseNetwork = NewsParser.fromJson(response.body());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d(Const.TAG, "getNews Parsing error");
                            return;
                        }
                        safeToDb(newsResponseNetwork);
//                        reschedule(10000); //10 sec
                        reschedule(60*60*1000); //1 hour
                    } else {
                        int statusCode = response.code();
                        Log.d(Const.TAG, "getNews onResponse: status code = "+statusCode);
                        Log.d(Const.TAG, "updateNews onResponse: response = "+response.raw().request().url());
                        rescheduleFail();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d(Const.TAG, "getNews onFalure: "+t.getMessage());
                    rescheduleFail();
                }
            });
        } else {
            Long diff = new Date().getTime() - lastUpdated.getTime();
            if (TimeUnit.MILLISECONDS.toHours(diff)<1){
                //too soon
                return;
            }
            //https://newsapi.org/v2/everything?q=android&from=2018-02-12T13:54:40Z&apiKey=7937bcf0615d4283bf3dcd18240a7f73
            new ServiceProvider().getService().updateNews(DateFormater.getNetworkString(lastUpdated),sources).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful()) {
                        NewsResponse newsResponseNetwork;
                        try {
                            newsResponseNetwork = NewsParser.fromJson(response.body());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d(Const.TAG, "updateNews Parsing error");
                            return;
                        }
                        safeToDb(newsResponseNetwork);
//                        reschedule(10000); //10 sec
                        reschedule(60*60*1000); //1 hour
                    } else {
                        int statusCode = response.code();
                        Log.d(Const.TAG, "updateNews onResponse: status code = "+statusCode);
                        Log.d(Const.TAG, "updateNews onResponse: response = "+response.raw().request().url());
                        rescheduleFail();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d(Const.TAG, "getNews onFalure: "+t.getMessage());
                    rescheduleFail();
                }
            });
        }

        new ServiceProvider().getService().getSources().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    SourcesResponse sourcesResponseNetwork;
                    try {
                        sourcesResponseNetwork = SourcesParser.fromJson(response.body());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.d(Const.TAG, "getSources Parsing error");
                        return;
                    }
                    safeSourcesToDb(sourcesResponseNetwork);
                } else {
                    int statusCode = response.code();
                    Log.d(Const.TAG, "getSources onResponse: status code = "+statusCode);
                    rescheduleFail();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d(Const.TAG, "getNews onFalure: "+t.getMessage());
                rescheduleFail();
            }
        });
    }

    private void rescheduleFail(){
        failInterval = failInterval * 2;
        if (failInterval > maxInterval) {
            failInterval = maxInterval;
        }
        reschedule(failInterval);
    }

    private void reschedule(int mills) {
        if (alarmManager == null) {
            Log.d(Const.TAG, "cannot reschedule Alarm Manager is not awailable");
            return;
        }
        alarmManager.cancel(pendingIntent);
        Log.d(Const.TAG, "Internet Loader reschedule for: " + mills);
        Intent serviceIntent = new Intent(getApplicationContext(), NetworkScheduler.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), InternetLoaderService.SERVICE_ID,  serviceIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + mills, pendingIntent);
    }

    void safeToDb(NewsResponse response) {
        ContentResolver cr = getApplicationContext().getContentResolver();
        for (int i=0; i<response.getArticles().size(); i++){
            Article article = response.getArticles().get(i);

            //insert source
            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.KEY_SOURCE_ID, article.getSource().getId());
            values.put(DatabaseHandler.KEY_NAME, article.getSource().getName());

            cr.insert(NewsProvider.SOURCES_URI, values);

            //insert article
            values = new ContentValues();
            values.put(DatabaseHandler.KEY_SOURCE_ID, article.getSource().getId());
            values.put(DatabaseHandler.KEY_AUTHOR, article.getAuthor());
            values.put(DatabaseHandler.KEY_TITLE, article.getTitle());
            values.put(DatabaseHandler.KEY_DESCRIPTION, article.getDescription());
            values.put(DatabaseHandler.KEY_URL, article.getUrl());
            values.put(DatabaseHandler.KEY_URL_TO_IMAGE, article.getUrlToImage());
            values.put(DatabaseHandler.KEY_PUBLISHED_AT, article.getPublishedAtFullString());
            values.put(DatabaseHandler.KEY_IS_DELETED, article.isDeleted()?1:0);
            values.put(DatabaseHandler.KEY_IS_READ, article.isRead()?1:0);

            cr.insert(NewsProvider.ARTICLES_URI, values);
        }
        saveLastUpdated();
    }

    void safeSourcesToDb(SourcesResponse response) {
        ContentResolver cr = getApplicationContext().getContentResolver();
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
}
