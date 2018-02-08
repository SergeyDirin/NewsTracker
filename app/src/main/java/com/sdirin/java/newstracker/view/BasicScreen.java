package com.sdirin.java.newstracker.view;

import com.sdirin.java.newstracker.data.SelectedSources;
import com.sdirin.java.newstracker.data.database.DatabaseHandler;

/**
 * Created by User on 07.02.2018.
 */

public interface BasicScreen {

    DatabaseHandler getDb();

    SelectedSources getSelectedSources();

    void showErrorMessage();

    void displayList();

    void logD(String message);

    boolean isInternetAvailable();
    boolean isPermitionGranted();
    void askPermition();
}
