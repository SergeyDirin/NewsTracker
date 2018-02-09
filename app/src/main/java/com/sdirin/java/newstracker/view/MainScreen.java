package com.sdirin.java.newstracker.view;

import android.content.ContentResolver;

import com.sdirin.java.newstracker.data.model.NewsResponse;

/**
 * Created by SDirin on 06-Jan-18.
 */

public interface MainScreen extends BasicScreen {

    void setNewsResponse(NewsResponse newsResponse);

    ContentResolver getContentResolver();
}
