package com.sdirin.java.newstracker.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.sdirin.java.newstracker.data.database.DatabaseHandler;

import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_IS_DELETED;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_PUBLISHED_AT;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_SOURCE_ID;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.TABLE_ARTICLES;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.TABLE_SOURCES;

public class NewsProvider extends ContentProvider {

    public static final String AUTHORITY = "com.sdirin.newstracker";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final Uri ARTICLES_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_ARTICLES);
    DatabaseHandler dbh;


    static {
        sUriMatcher.addURI(AUTHORITY, TABLE_ARTICLES, 1);
        sUriMatcher.addURI(AUTHORITY, TABLE_SOURCES, 2);
    }

    public NewsProvider(){
    }

    @Override
    public boolean onCreate() {
        dbh = new DatabaseHandler(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentResolver cr = getContext().getContentResolver();
        Cursor c;
        switch (sUriMatcher.match(uri)) {
            case 1:
                c = db.rawQuery("SELECT a.*,s.* FROM "+
                                TABLE_ARTICLES+" a, " + TABLE_SOURCES +
                                " s WHERE a."+KEY_SOURCE_ID+"=s."+KEY_SOURCE_ID+" AND a."+KEY_IS_DELETED+"=0 ORDER BY "+KEY_PUBLISHED_AT+" DESC",
                        null);
                c.setNotificationUri(cr, uri);
                return c;
            case 2:
                c = db.query(TABLE_SOURCES,projection,selection,selectionArgs,"","",sortOrder);
                c.setNotificationUri(cr, uri);
                return c;
            default:
                throw new UnsupportedOperationException("Wrong Uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        long id;
        ContentResolver cr = getContext().getContentResolver();
        switch (sUriMatcher.match(uri)) {
            case 1:
                id = db.insert(TABLE_ARTICLES,"",values);
                if (cr!=null){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return ContentUris.withAppendedId(uri,id);
            case 2:
                id = db.insert(TABLE_SOURCES,"",values);
                if (cr!=null){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return ContentUris.withAppendedId(uri,id);
            default:
                throw new UnsupportedOperationException("Wrong Uri");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        int id;
        switch (sUriMatcher.match(uri)) {
            case 1:
                id = db.delete(TABLE_ARTICLES,selection,selectionArgs);
                return id;
            case 2:
                id = db.delete(TABLE_SOURCES,selection,selectionArgs);
                return id;
            default:
                throw new UnsupportedOperationException("Wrong Uri");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        int id;
        switch (sUriMatcher.match(uri)) {
            case 1:
                id = db.update(TABLE_ARTICLES,values,selection,selectionArgs);
                return id;
            case 2:
                id = db.update(TABLE_SOURCES,values,selection,selectionArgs);
                return id;
            default:
                throw new UnsupportedOperationException("Wrong Uri");
        }
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
