package com.sdirin.java.newstracker.data.network;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sdirin.java.newstracker.activities.MainActivity;
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
import com.sdirin.java.newstracker.utils.DateFormater;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 12.02.2018.
 */

public class InternetLoader extends JobService {
    private static final String PREF_LAST_UPDATED = "com.sdirin.java.newstracker.lastupdated";
    DownloadWorker worker;
    JobParameters parameters;
    public String sources;

    //todo save lastUpdate to storage
    static Date lastUpdated;

    @Override
    public boolean onStartJob(JobParameters params) {
        worker = new DownloadWorker(this);
        this.parameters = params;

        PersistableBundle extra = params.getExtras();
        if (extra.containsKey(MainActivity.SELECTED_SOURCES)){
            sources = extra.getString(MainActivity.SELECTED_SOURCES);
        }

        loadLastUpdated();

        worker.execute(new String[] {sources});
        return true;
    }

    private void loadLastUpdated() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                SelectedSources.PREF, Context.MODE_PRIVATE);
        long prefLastUpdated = prefs.getLong(PREF_LAST_UPDATED, 0);
        if (prefLastUpdated != 0) {
            lastUpdated = new Date(prefLastUpdated);
        }
    }

    private void saveLastUpdated(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                SelectedSources.PREF, Context.MODE_PRIVATE);
        prefs.edit().putLong(PREF_LAST_UPDATED, lastUpdated.getTime()).apply();
    }



    @Override
    public boolean onStopJob(JobParameters params) {
        worker.cancel(true);
        return true;
    }


    private static class DownloadWorker extends AsyncTask<String,Void,Void> {

        private final InternetLoader loader;

        public DownloadWorker(InternetLoader loader){
            this.loader = loader;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loader.jobFinished(loader.parameters,false);
        }

        @Override
        protected Void doInBackground(String... pSources) {

            if (lastUpdated == null){
                lastUpdated = new Date();
                if (pSources.length == 0){
                    return null;
                }
                String sources = pSources[0];
                if (sources.length() == 0){
                    sources = "polygon";
                }
                new ServiceProvider().getService().getNews(sources).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            NewsResponse newsResponseNetwork;
                            try {
                                newsResponseNetwork = NewsParser.fromJson(response.body());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.d(MainActivity.TAG, "getNews Parsing error");
                                return;
                            }
                            safeToDb(newsResponseNetwork);
                        } else {
                            int statusCode = response.code();
                            Log.d(MainActivity.TAG, "getNews onResponse: status code = "+statusCode);
                            loader.worker.cancel(true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d(MainActivity.TAG, "getNews onFalure: "+t.getMessage());
                        loader.worker.cancel(true);
                    }
                });
            } else {
                Long diff = new Date().getTime() - lastUpdated.getTime();
                if (TimeUnit.MILLISECONDS.toHours(diff)<1){
                    //too soon
                    return null;
                }
                //https://newsapi.org/v2/everything?q=android&from=2018-02-12T13:54:40Z&apiKey=7937bcf0615d4283bf3dcd18240a7f73
                new ServiceProvider().getService().updateNews(DateFormater.getNetworkString(lastUpdated)).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            NewsResponse newsResponseNetwork;
                            try {
                                newsResponseNetwork = NewsParser.fromJson(response.body());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.d(MainActivity.TAG, "updateNews Parsing error");
                                return;
                            }
                            safeToDb(newsResponseNetwork);
                        } else {
                            int statusCode = response.code();
                            Log.d(MainActivity.TAG, "updateNews onResponse: status code = "+statusCode);
                            loader.worker.cancel(true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d(MainActivity.TAG, "getNews onFalure: "+t.getMessage());
                        loader.worker.cancel(true);
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
                            Log.d(MainActivity.TAG, "getSources Parsing error");
                            return;
                        }
                        safeSourcesToDb(sourcesResponseNetwork);
                    } else {
                        int statusCode = response.code();
                        Log.d(MainActivity.TAG, "getSources onResponse: status code = "+statusCode);
                        loader.worker.cancel(true);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d(MainActivity.TAG, "getNews onFalure: "+t.getMessage());
                    loader.worker.cancel(true);
                }
            });

            return null;
        }

        void safeToDb(NewsResponse response) {
            ContentResolver cr = loader.getContentResolver();
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
            loader.saveLastUpdated();
        }

        void safeSourcesToDb(SourcesResponse response) {
            ContentResolver cr = loader.getContentResolver();
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
}
