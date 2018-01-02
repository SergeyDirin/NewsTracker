package com.sdirin.java.newstracker.data.network.mock;

import android.support.annotation.NonNull;
import android.support.coreutils.BuildConfig;

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
    // FAKE RESPONSES.
    private final static String OK_RESP = "{\"status\":\"ok\",\"totalResults\":2,\"articles\":[{\"source\":{\"id\":\"polygon\",\"name\":\"Polygon\"},\"author\":\"Colin Campbell\",\"title\":\"Civilization 6 on iPad is a marvel\",\"description\":\"Civilization 6 was recently released on iPad, which came as something of a surprise. Publisher Aspyr Media had made no previous announcements. This is not some lite version of Civ stripped down for...\",\"url\":\"https://www.polygon.com/2018/1/1/16830478/civilization-6-ipad-review\",\"urlToImage\":\"https://cdn.vox-cdn.com/thumbor/neeuSotMczWDUScxEt2Gs25KmcA=/0x14:700x380/fit-in/1200x630/cdn.vox-cdn.com/uploads/chorus_asset/file/9946533/ss_f501156a69223131ee8b12452f3003698334e964.1920x1080.jpg\",\"publishedAt\":\"2018-01-01T18:30:02Z\"},{\"source\":{\"id\":\"polygon\",\"name\":\"Polygon\"},\"author\":\"Owen S. Good\",\"title\":\"Mutant Football League kicks off the New Year with a console launch\",\"description\":\"Undead players don’t need a concussion protocol\",\"url\":\"https://www.polygon.com/2018/1/1/16838278/mutant-football-league-xbox-one-playstation-4-launch\",\"urlToImage\":\"https://cdn.vox-cdn.com/thumbor/H1FD1rcEuxmjPle66-6qlX4wVaY=/0x38:1920x1043/fit-in/1200x630/cdn.vox-cdn.com/uploads/chorus_asset/file/9958311/MFL_2016_08_01_21_57_07_24.jpg\",\"publishedAt\":\"2018-01-01T18:00:02Z\"}]}";
    private final static String WRONG_APIKEY = "{\"status\":\"error\",\"code\":\"apiKeyInvalid\",\"message\":\"Your API key is invalid or incorrect. Check your key, or go to https://newsapi.org to create a free API key.\"}";
//    public static final String NO_API = "";
    public static final String NO_API = "{\"status\":\"error\",\"code\":\"apiKeyMissing\",\"message\":\"Your API key is missing. Append this to the URL with the apiKey param, or use the x-api-key HTTP header.\"}";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = null;
        if(BuildConfig.DEBUG) {
            String responseString;
            // Get Request URI.
            HttpUrl url = chain.request().url();
            //@GET("top-headlines?sources=polygon&apiKey=7937bcf0615d4283bf3dcd18240a7f73")
            //match request with response
            if (!url.queryParameterNames().contains("apiKey")){
                responseString = NO_API;
            }
            else if (!url.queryParameter("apiKey").equals("7937bcf0615d4283bf3dcd18240a7f73")){
                responseString = WRONG_APIKEY;
            }
            else if(url.queryParameter("sources").equals("polygon")) {
                responseString = OK_RESP;
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
