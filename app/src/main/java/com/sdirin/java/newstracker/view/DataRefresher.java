package com.sdirin.java.newstracker.view;

/**
 * Created by User on 08.02.2018.
 */

public interface DataRefresher {
    void refresh();
    void removeArticle(int dbId);
}
