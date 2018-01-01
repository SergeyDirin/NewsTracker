package com.sdirin.java.newstracker.data.model;

import java.util.List;

/**
 * Created by SDirin on 01-Jan-18.
 */

public class NewsResponse {
//    {
//        "status": "ok",
//            "totalResults": 4,
//            -"articles": [
//        +{ … },
//        +{ … },
//        +{ … },
//        +{ … }
//]
//    }

    private String status;
    private List<Article> articles;

    public NewsResponse(String status, List<Article> articles) {
        this.status = status;
        this.articles = articles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
