package com.sdirin.java.newstracker.data.network;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by SDirin on 01-Jan-18.
 */

public interface NewsService {

    //host
    //https://newsapi.org/v2/

    //https://newsapi.org/v2/top-headlines?sources=polygon&apiKey=7937bcf0615d4283bf3dcd18240a7f73
    @GET("top-headlines?sources=polygon&apiKey=7937bcf0615d4283bf3dcd18240a7f73")
    Call<String> getNews();

}
