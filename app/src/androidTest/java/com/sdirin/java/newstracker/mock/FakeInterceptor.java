package com.sdirin.java.newstracker.mock;

import android.support.annotation.NonNull;
import android.support.coreutils.BuildConfig;

import com.sdirin.java.newstracker.utils.Requests;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by SDirin on 02-Jan-18.
 */

public class FakeInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response;
        if(BuildConfig.DEBUG) {
            String responseString;
            // Get Request URI.
            HttpUrl url = chain.request().url();
            //@GET("top-headlines?sources=polygon&apiKey=7937bcf0615d4283bf3dcd18240a7f73")
            //match request with response
            if (!url.queryParameterNames().contains("apiKey")){
                responseString = Requests.NO_API;
            }
            else if (!url.queryParameter("apiKey").equals("7937bcf0615d4283bf3dcd18240a7f73")){
                responseString = Requests.WRONG_APIKEY;
            }
            else if(url.queryParameter("sources").equals("polygon")) {
                responseString = Requests.OK_RESP;
            }
            else {
                responseString = "";
            }

            response = new Response.Builder()
                    .code(200)
                    .message(responseString)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                    .addHeader("content-type", "application/json")
                    .build();
        }
        else {
            response = chain.proceed(chain.request());
        }

        return response;
    }
}
