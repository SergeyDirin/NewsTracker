package com.sdirin.java.newstracker.data.network;

import retrofit2.Retrofit;

/**
 * Created by SDirin on 01-Jan-18.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).build();
        }
        return retrofit;
    }

}
