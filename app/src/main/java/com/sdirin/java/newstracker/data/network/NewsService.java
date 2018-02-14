package com.sdirin.java.newstracker.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by SDirin on 01-Jan-18.
 */

public interface NewsService {

    //host
    //https://newsapi.org/v2/

    //https://newsapi.org/v2/top-headlines?sources=polygon&apiKey=7937bcf0615d4283bf3dcd18240a7f73
    @GET("top-headlines?apiKey=7937bcf0615d4283bf3dcd18240a7f73")
    Call<String> getNews(@Query("sources") String sources);

    //https://newsapi.org/v2/sources?category=entertainment&apiKey=7937bcf0615d4283bf3dcd18240a7f73
    @GET("sources?category=entertainment&apiKey=7937bcf0615d4283bf3dcd18240a7f73")
    Call<String> getSources();

    //https://newsapi.org/v2/sources?category=entertainment&apiKey=7937bcf0615d4283bf3dcd18240a7f73
    @GET("sources?category=entertainment&apiKey=7937bcf0615d4283bf3dcd18240a7f73")
    Call<String> updateNews(@Query("from") String fromDate);

}
