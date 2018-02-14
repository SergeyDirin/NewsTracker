package com.sdirin.java.newstracker.presenters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.sdirin.java.newstracker.data.NewsProvider;
import com.sdirin.java.newstracker.data.ServiceProvider;
import com.sdirin.java.newstracker.data.model.Article;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.view.MainScreen;

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
