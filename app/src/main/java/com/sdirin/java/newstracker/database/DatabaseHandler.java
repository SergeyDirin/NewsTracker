package com.sdirin.java.newstracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sdirin.java.newstracker.data.model.Article;
import com.sdirin.java.newstracker.data.model.Source;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDirin on 04-Jan-18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "newsTrackerDatabse";
    public static final String TABLE_ARTICLES = "articles";
    public static final String TABLE_SOURCES = "sources";
    /**
     * Table Articles fields
     * private Source source;
     private String author;
     private String title;
     private String description;
     private String url;
     private String urlToImage;
     private Date publishedAt
     */
    public static final String KEY_ID = "id";
    public static final String KEY_SOURCE_ID = "source_id";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_URL = "url";
    public static final String KEY_URL_TO_IMAGE = "url_to_image";
    public static final String KEY_PUBLISHED_AT = "published_at";
    public static final String KEY_IS_DELETED = "is_deleted";

    /**
     * Table Sources fields
     *  private String id;
     private String name;
     *
     */
    public static final String KEY_NAME = "name";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_articles_table = "CREATE TABLE " + TABLE_ARTICLES + "(" +
                KEY_ID +" INTEGER PRIMARY KEY, " +
                KEY_SOURCE_ID + " TEXT," +
                KEY_AUTHOR + " TEXT," +
                KEY_TITLE + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_URL + " TEXT," +
                KEY_URL_TO_IMAGE + " TEXT," +
                KEY_PUBLISHED_AT + " TEXT," +
                KEY_IS_DELETED + " INTEGER" +")";
        db.execSQL(create_articles_table);
        String create_sources_table = "CREATE TABLE " + TABLE_SOURCES + "(" +
                KEY_ID +" INTEGER PRIMARY KEY, " +
                KEY_SOURCE_ID +" TEXT, " +
                KEY_NAME + " TEXT" +")";
        db.execSQL(create_sources_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SOURCES);
        onCreate(db);
    }

    /**
     * Sources CRUD
     */

    public void addSource(Source source){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE_ID, source.getId());
        values.put(KEY_NAME, source.getName());

        db.insert(TABLE_SOURCES, null, values);
        db.close();
    }

    public Source getSource(String source_id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_SOURCES,
                new String[]{KEY_ID,KEY_SOURCE_ID,KEY_NAME},
                KEY_SOURCE_ID + "=?",
                new String[]{source_id},null,null,null);
        if (cursor == null) {
            return null;
        }

        if (!cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        Source source = new Source(cursor.getString(1),
                cursor.getString(2));
        cursor.close();
        return source;
    }

    public List<Source> getAllSources(){
        List<Source> sourceList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SOURCES, null);

        if (cursor == null) {
            return null;
        }

        if (cursor.moveToFirst()){
            do {
                Source source = new Source(cursor.getString(1),cursor.getString(2));
                sourceList.add(source);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return sourceList;
    }

    public int getSourcesCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_SOURCES, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateSource(Source source){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,source.getName());

        return db.update(TABLE_SOURCES, values, KEY_SOURCE_ID + " = ?",
                new String[]{source.getId()});
    }

    public void deleteSource(Source source){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SOURCES, KEY_SOURCE_ID + " = ?",
                new String[]{source.getId()});
        db.close();
    }

    /**
     * Articles CRUD
     */
    public void addArticle(Article article){

        Source source = getSource(article.getSource().getId());
        if (source == null){
            addSource(article.getSource());
        }
        Article oldArticle = getArticleByTitle(article.getTitle());
        if (oldArticle != null){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE_ID, article.getSource().getId());
        values.put(KEY_AUTHOR, article.getAuthor());
        values.put(KEY_TITLE, article.getTitle());
        values.put(KEY_DESCRIPTION, article.getDescription());
        values.put(KEY_URL, article.getUrl());
        values.put(KEY_URL_TO_IMAGE, article.getUrlToImage());
        values.put(KEY_PUBLISHED_AT, article.getPublishedAtFullString());
        values.put(KEY_IS_DELETED, 0);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ARTICLES, null, values);
        db.close();
    }

    public Article getArticle(int id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_ARTICLES,
                new String[]{KEY_ID,
                        KEY_SOURCE_ID,
                        KEY_AUTHOR,
                        KEY_TITLE,
                        KEY_DESCRIPTION,
                        KEY_URL,
                        KEY_URL_TO_IMAGE,
                        KEY_PUBLISHED_AT,
                        KEY_IS_DELETED},
                KEY_ID + "=?",
                new String[]{Integer.toString(id)},null,null,null);
        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();
        Article article = new Article();
        article.setDbId(Integer.parseInt(cursor.getString(0)));
        article.setAuthor(cursor.getString(2));
        article.setTitle(cursor.getString(3));
        article.setDescription(cursor.getString(4));
        article.setUrl(cursor.getString(5));
        article.setUrlToImage(cursor.getString(6));
        try {
            article.setPublishedAtString(cursor.getString(7));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Source source = getSource(cursor.getString(1));
        if (source == null){
            cursor.close();
            return null;
        }
        article.setSource(source);
        cursor.close();
        return article;
    }

    public Article getArticleByTitle(String title){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_ARTICLES,
                new String[]{KEY_ID,
                        KEY_SOURCE_ID,
                        KEY_AUTHOR,
                        KEY_TITLE,
                        KEY_DESCRIPTION,
                        KEY_URL,
                        KEY_URL_TO_IMAGE,
                        KEY_PUBLISHED_AT},
                KEY_TITLE + "=?",
                new String[]{title},null,null,null);
        if (cursor == null) {
            return null;
        }

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        Article article = new Article();
        article.setDbId(Integer.parseInt(cursor.getString(0)));
        article.setAuthor(cursor.getString(2));
        article.setTitle(cursor.getString(3));
        article.setDescription(cursor.getString(4));
        article.setUrl(cursor.getString(5));
        article.setUrlToImage(cursor.getString(6));
        try {
            article.setPublishedAtString(cursor.getString(7));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Source source = getSource(cursor.getString(1));
        if (source == null){
            cursor.close();
            return null;
        }
        article.setSource(source);
        cursor.close();
        return article;
    }

    public List<Article> getAllArticles(){
        List<Article> articleList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT a.*,s.name FROM "+TABLE_ARTICLES+" a, " + TABLE_SOURCES + " s WHERE a."+KEY_SOURCE_ID+"=s."+KEY_SOURCE_ID+" AND a."+KEY_IS_DELETED+"=0",null);

        if (cursor == null) {
            return null;
        }

        if (cursor.moveToFirst()){
            do {
                Source source = new Source(cursor.getString(cursor.getColumnIndex(KEY_SOURCE_ID)),cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                if (cursor.getInt(cursor.getColumnIndex(KEY_IS_DELETED)) == 0) {
                    Article article = new Article(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                            source,
                            cursor.getString(cursor.getColumnIndex(KEY_AUTHOR)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_URL)),
                            cursor.getString(cursor.getColumnIndex(KEY_URL_TO_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(KEY_PUBLISHED_AT))
                    );
                    articleList.add(article);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return articleList;
    }


    public int getArticlesCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_ARTICLES, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateArticle(Article article){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SOURCE_ID, article.getSource().getId());
        values.put(KEY_AUTHOR, article.getAuthor());
        values.put(KEY_TITLE, article.getTitle());
        values.put(KEY_DESCRIPTION, article.getDescription());
        values.put(KEY_URL, article.getUrl());
        values.put(KEY_URL_TO_IMAGE, article.getUrlToImage());
        values.put(KEY_PUBLISHED_AT, article.getPublishedAtFullString());

        if (article.getDbId() < 0){
            return db.update(TABLE_ARTICLES, values, KEY_TITLE + " = ?",
                    new String[]{article.getTitle()});
        } else {
            return db.update(TABLE_ARTICLES, values, KEY_ID + " = ?",
                    new String[]{Integer.toString(article.getDbId())});
        }
    }

    public void deleteArticle(Article article){
        SQLiteDatabase db = getWritableDatabase();
        if (article.getDbId() < 0){
            ContentValues values = new ContentValues();
            values.put(KEY_IS_DELETED, 1);
            db.update(TABLE_ARTICLES, values, KEY_TITLE + " = ?",
                    new String[]{article.getTitle()});
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_DELETED, 1);
            db.update(TABLE_ARTICLES, values, KEY_ID + " = ?",
                    new String[]{Integer.toString(article.getDbId())});
        }
        db.close();
    }

}



















