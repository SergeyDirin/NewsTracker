package com.sdirin.java.newstracker.view;

import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.database.DatabaseHandler;

/**
 * Created by SDirin on 06-Jan-18.
 */

public interface MainScreen {

    DatabaseHandler getDb();

    void showErrorMessage();

    void displayList();

    void setNewsResponse(NewsResponse newsResponse);

    void logD(String message);

    boolean isInternetAvailable();
}
