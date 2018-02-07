package com.sdirin.java.newstracker.data.model;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by SDirin on 01-Jan-18.
 */

public class Article implements Comparable<Article> {

    //    articles": [
//            -{
//        -"source": {
//            "id": "techcrunch",
//                    "name": "TechCrunch"
//        },
//        "author": "Matthew Lynley",
//                "title": "Twitter ended the year on a fascinating run",
//                "description": "It's been pretty easy to point at Twitter and, with each quarterly moment when it discloses its financial guts, let out a long exasperated sigh. But then..",
//                "url": "https://techcrunch.com/2017/12/31/twitter-ended-the-year-on-a-fascinating-run/",
//                "urlToImage": "https://tctechcrunch2011.files.wordpress.com/2017/06/gettyimages-497874484.jpg",
//                "publishedAt": "2017-12-31T16:06:28Z"
//    }
    private int dbId = -1;
    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private Date publishedAt;
    private boolean isRead;
    private boolean isDeleted;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Article() {}

    public Article(int dbId, Source source, String author, String title, String description, String url, String urlToImage, Date publishedAt, boolean isRead, boolean isDeleted) {
        this.dbId = dbId;
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.isRead = isRead;
        this.isDeleted = isDeleted;
    }
    public Article(int dbId, Source source, String author, String title, String description, String url, String urlToImage, String publishedAt, boolean isRead, boolean isDeleted) {
        this.dbId = dbId;
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        try {
            setPublishedAtString(publishedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.isRead = isRead;
        this.isDeleted = isDeleted;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getPublishedAtString() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(publishedAt);
    }

    public String getPublishedAtFullString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(publishedAt);
    }

    public void setPublishedAtString(String publishedAt) throws ParseException {
        String prepare = publishedAt.replace('T',' ').replace("Z","");//"2009-10-10T12:12:12Z";
        SimpleDateFormat sdf;
        //check if already formated
        if (publishedAt.length() < 10){
            sdf = new SimpleDateFormat("d MMM", Locale.getDefault());
        } else {
            sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC (+000)"));
        }
        this.publishedAt = sdf.parse(prepare);
    }

    public String getID(){
        return title;
    }

    @Override
    public int compareTo(@NonNull Article o) {
        return o.getPublishedAt().compareTo(getPublishedAt());
    }
}
