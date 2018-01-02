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
//        {
//            "source": {
//                "id": "polygon",
//                "name": "Polygon"
//             },
//            "author": "Michael McWhertor",
//            "title": "Mario and Zelda topped Amazon’s 2017 best-selling games list",
//            "description": "Amazon shoppers also bought a lot of PlayStation Store credit",
//            "url": "https://www.polygon.com/2018/1/1/16838638/amazon-best-selling-games-2017-mario-zelda-switch",
//            "urlToImage": "https://cdn.vox-cdn.com/thumbor/SftEufeghLD6BLlLOeB96g-LMWQ=/0x19:1280x689/fit-in/1200x630/cdn.vox-cdn.com/uploads/chorus_asset/file/9958985/Switch_SMO_ND0913_SCRN07.jpg",
//            "publishedAt": "2018-01-01T20:00:02Z"
//        },
//        +{ … },
//        +{ … },
//        +{ … }
//]
//    }

/*
    {
        "status": "error",
        "code": "apiKeyInvalid",
        "message": "Your API key is invalid or incorrect. Check your key, or go to https://newsapi.org to create a free API key."
    }
*/

    private String status;
    private List<Article> articles;
    private String code;
    private String message;

    public NewsResponse() {}

    //constructor for ok response
    public NewsResponse(String status, List<Article> articles) {
        this.status = status;
        this.articles = articles;
    }

    //constructor for error response
    public NewsResponse(String status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
