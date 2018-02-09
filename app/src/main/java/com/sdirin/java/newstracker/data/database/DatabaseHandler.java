package com.sdirin.java.newstracker.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public static final String KEY_IS_READ = "is_read";

    /**
     * Table Sources fields
     *  private String id;
     private String name;
     *
     */
    public static final String KEY_NAME = "name";
    public static final String KEY_URL_SOURCE = "url_source";
    public static final String KEY_DESCRIPTION_SOURCE = "description_source";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_COUNTRY = "country";


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
                KEY_IS_DELETED + " INTEGER," +
                KEY_IS_READ + " INTEGER" +")";
        db.execSQL(create_articles_table);
        String create_sources_table = "CREATE TABLE " + TABLE_SOURCES + "(" +
                KEY_ID +" INTEGER PRIMARY KEY, " +
                KEY_SOURCE_ID +" TEXT, " +
                KEY_NAME + " TEXT, " +
                KEY_DESCRIPTION_SOURCE + " TEXT, " +
                KEY_URL_SOURCE + " TEXT, " +
                KEY_CATEGORY + " TEXT, " +
                KEY_LANGUAGE + " TEXT, " +
                KEY_COUNTRY + " TEXT" +")";
        db.execSQL(create_sources_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_SOURCES);
        onCreate(db);
    }
}



















