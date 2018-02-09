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

import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_ID;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_IS_DELETED;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_NAME;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_PUBLISHED_AT;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_SOURCE_ID;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_TITLE;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.TABLE_ARTICLES;
import static com.sdirin.java.newstracker.data.database.DatabaseHandler.TABLE_SOURCES;

public class NewsProvider extends ContentProvider {

    public static final String AUTHORITY = "com.sdirin.newstracker";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final Uri ARTICLES_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_ARTICLES);
    public static final Uri SOURCES_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_SOURCES);
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
                c = db.rawQuery("SELECT a.*,s."+KEY_SOURCE_ID+",s."+KEY_NAME+" FROM "+
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
        if (sUriMatcher.match(uri) == 1){
            //check if already exists
            Cursor c = db.rawQuery("SELECT * FROM "+
                            TABLE_ARTICLES+" WHERE "+KEY_TITLE+" = ?",
                    new String[] {values.getAsString(KEY_TITLE)});
            if (c.getCount() > 0){
                c.moveToFirst();
//                    Log.d(TAG,"found same article" + c.getLong(c.getColumnIndex(KEY_ID)));
                return ContentUris.withAppendedId(uri,c.getLong(c.getColumnIndex(KEY_ID)));
            }
            //insert othervise.
            id = db.insert(TABLE_ARTICLES,"",values);
            db.close();
            if (cr!=null){
                cr.notifyChange(uri, null);
            }
            return ContentUris.withAppendedId(uri,id);
        } else if (sUriMatcher.match(uri) == 2) {
            //check if already exists
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SOURCES +
                            " WHERE "+KEY_SOURCE_ID+" = ?",
                    new String[] {values.getAsString(KEY_SOURCE_ID)});
            if (c.getCount() > 0){
                c.moveToFirst();
                update(uri,values,KEY_SOURCE_ID + " = ?",new String[]{c.getString(c.getColumnIndex(KEY_SOURCE_ID))});
                return ContentUris.withAppendedId(uri,c.getLong(c.getColumnIndex(KEY_SOURCE_ID)));
            }
            //insert othervise.
            id = db.insert(TABLE_SOURCES,"",values);
            db.close();
            if (cr!=null){
                cr.notifyChange(uri, null);
            }
            return ContentUris.withAppendedId(uri,id);
        } else {
            throw new UnsupportedOperationException("Wrong Uri");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        int id;
        ContentResolver cr = getContext().getContentResolver();
        switch (sUriMatcher.match(uri)) {
            case 1:
                id = db.delete(TABLE_ARTICLES,selection,selectionArgs);
                if (cr!=null){
                    cr.notifyChange(uri, null);
                }
                return id;
            case 2:
                id = db.delete(TABLE_SOURCES,selection,selectionArgs);
                if (cr!=null){
                    cr.notifyChange(uri, null);
                }
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
        ContentResolver cr = getContext().getContentResolver();
        switch (sUriMatcher.match(uri)) {
            case 1:
                id = db.update(TABLE_ARTICLES,values,selection,selectionArgs);
                if (cr!=null){
                    cr.notifyChange(uri, null);
                }
                return id;
            case 2:
                id = db.update(TABLE_SOURCES,values,selection,selectionArgs);
                if (cr!=null){
                    cr.notifyChange(uri, null);
                }
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
