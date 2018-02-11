package com.sdirin.java.newstracker;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.sdirin.java.newstracker.data.database.DatabaseHandler;

/**
 * Created by User on 11.02.2018.
 */

public class CleanUpService extends IntentService {

    public CleanUpService() {
        super("ClenUp Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //create dbhandler
        DatabaseHandler dbh = new DatabaseHandler(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        //execute operation
        db.rawQuery("UPDATE "+DatabaseHandler.TABLE_ARTICLES+" SET " +
                DatabaseHandler.KEY_AUTHOR + " = '', " +
                DatabaseHandler.KEY_DESCRIPTION + " = '', " +
                DatabaseHandler.KEY_URL + " = '', " +
                DatabaseHandler.KEY_URL_TO_IMAGE + " = '', " +
                DatabaseHandler.KEY_IS_READ + " = 1 " +
                "WHERE "+DatabaseHandler.KEY_IS_DELETED + " > 0 ",new String[]{});
        db.close();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "DB is clened", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
