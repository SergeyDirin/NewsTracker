package com.sdirin.java.newstracker.data.network;

import android.support.annotation.NonNull;

import com.sdirin.java.newstracker.data.network.mock.FakeInterceptor;
import com.sdirin.java.newstracker.data.network.mock.Requests;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by SDirin on 02-Jan-18.
 */
public class RetrofitClientTest {
    //fake testing url
    private static final String BASE_URL = "http://fantasy.world.com/";
    NewsService service;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void checkRequestArticles() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new FakeInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(NewsService.class);

        service.getNews("polygon").enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    Assert.assertTrue(response.body().contains("\"status\":\"ok\""));
                } else {
                    int statusCode = response.code();
                    throw new AssertionError("Status code is not OK = " + statusCode);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                throw new AssertionError("Request Failed", t);
            }
        });
    }

    @Test
    public void checkTooManyRequests() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                        HttpUrl url = chain.request().url();

                        String responseString = Requests.TOO_MANY_REQUESTS;
                        return new okhttp3.Response.Builder()
                                .code(429)
                                .message(responseString)
                                .request(chain.request())
                                .protocol(Protocol.HTTP_1_0)
                                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                                .addHeader("content-type", "application/json")
                                .build();
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(NewsService.class);

        service.getNews("polygon").enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    throw new AssertionError("Status code is OK " );
                } else {
                    int statusCode = response.code();
                    Assert.assertTrue(response.body().contains("\"code\":\"sourcesTooMany\""));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                throw new AssertionError("Request Failed", t);
            }
        });
    }

    @After
    public void tearDown() throws Exception {
    }
}