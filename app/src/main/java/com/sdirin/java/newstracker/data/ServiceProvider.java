package com.sdirin.java.newstracker.data;

import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.data.network.RetrofitClient;

/**
 * Created by SDirin on 01-Jan-18.
 */

public class ServiceProvider {
    //getNews
    //top-headlines?sources=polygon&apiKey=7937bcf0615d4283bf3dcd18240a7f73
    public static final String BASE_HOST = "https://newsapi.org/";
    public static final String BASE_URL = BASE_HOST + "v2/";

    public NewsService getService(){
        return RetrofitClient.getClient(BASE_URL).create(NewsService.class);
    }
}
