package com.sdirin.java.newstracker.view;

import android.content.ContentResolver;

import com.sdirin.java.newstracker.data.SelectedSources;

/**
 * Created by User on 07.02.2018.
 */

public interface BasicScreen {

    SelectedSources getSelectedSources();

    void showErrorMessage();

    void logD(String message);

    boolean isInternetAvailable();
    boolean isPermitionGranted(String permission);
    void askPermition(String permission,int resultCode);

    ContentResolver getContentResolver();
}
