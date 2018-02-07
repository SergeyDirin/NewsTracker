package com.sdirin.java.newstracker.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 07.02.2018.
 */

public class SourcesResponse {
//    {
//        "status": "ok",
//            "sources": [
//        {
//            "id": "buzzfeed",
//                "name": "Buzzfeed",
//                "description": "BuzzFeed is a cross-platform, global network for news and entertainment that generates seven billion views each month.",
//                "url": "https://www.buzzfeed.com",
//                "category": "entertainment",
//                "language": "en",
//                "country": "us"
//        },
//        {
//            "id": "daily-mail",
//                "name": "Daily Mail",
//                "description": "All the latest news, sport, showbiz, science and health stories from around the world from the Daily Mail and Mail on Sunday newspapers.",
//                "url": "http://www.dailymail.co.uk/home/index.html",
//                "category": "entertainment",
//                "language": "en",
//                "country": "gb"
//        },
//        {
//            "id": "polygon",
//                "name": "Polygon",
//                "description": "Polygon is a gaming website in partnership with Vox Media. Our culture focused site covers games, their creators, the fans, trending stories and entertainment news.",
//                "url": "http://www.polygon.com",
//                "category": "entertainment",
//                "language": "en",
//                "country": "us"
//        }
//    ]
//    }

    private String status;
    private List<Source> sources;
    private String code;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
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

    public void combineWith(SourcesResponse sourcesResponse){
        if (sources == null) {
            sources = new ArrayList<>();
        }
        if (sourcesResponse.sources == null) {
            return;
        }
        List<Source> sourcesToAdd = new ArrayList<>();
        for (Source newSource : sourcesResponse.getSources()) {
            boolean found = false;
            for (Source source: sources) {
                if (source.getId().equals(newSource.getId())) {
                    if (source.getDescription() == null || !source.getDescription().equals(newSource.getDescription())) {
                        source.setDescription(newSource.getDescription());
                        source.setUrl(newSource.getUrl());
                        source.setCategory(newSource.getCategory());
                        source.setLanguage(newSource.getLanguage());
                        source.setCountry(newSource.getCountry());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                sourcesToAdd.add(newSource);
            }
        }

        sources.addAll(sourcesToAdd);
    }
}
