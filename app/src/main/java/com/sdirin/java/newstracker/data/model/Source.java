package com.sdirin.java.newstracker.data.model;

import android.database.Cursor;

import com.sdirin.java.newstracker.data.database.DatabaseHandler;

/**
 * Created by SDirin on 01-Jan-18.
 */

public class Source {

    private String id;
    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;

    public Source(String id, String name, String description, String url, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    public Source() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public static Source fromCursor(Cursor cursor) {
        if (cursor.getColumnIndex(DatabaseHandler.KEY_DESCRIPTION_SOURCE) >=0) {
            return new Source(
                    cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SOURCE_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_DESCRIPTION_SOURCE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_URL_SOURCE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CATEGORY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_LANGUAGE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_COUNTRY))
            );
        } else {
            Source result =  new Source();
            result.setId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_SOURCE_ID)));
            result.setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME)));
            return result;
        }
    }
}
