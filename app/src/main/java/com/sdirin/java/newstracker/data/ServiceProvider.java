package com.sdirin.java.newstracker.data;

import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.data.network.RetrofitClient;

/**
 * Created by SDirin on 01-Jan-18.
 */

public class ServiceProvider {

    public static final String BASE_HOST = "https://newsapi.org/";
    public static final String BASE_URL = BASE_HOST + "v2/";

    public NewsService getService(){
        return RetrofitClient.getClient(BASE_URL).create(NewsService.class);
    }
}
