package com.sdirin.java.newstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.sdirin.java.newstracker.data.ApiUtils;
import com.sdirin.java.newstracker.data.network.NewsService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NewsApp";
    NewsService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo create tests for network

        //todo add classes ro Json parse
        //todo prepare RecyclerView and show list

        mService = ApiUtils.getService();

        mService.getNews().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "resp = " + response.body(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: posts loaded from API = " + response.body());
                } else {
                    int statusCode = response.code();
                    Log.d(TAG, "onResponse: status code = "+statusCode);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showErrorMessage();
                Log.d(TAG, "onResponse: error loading from API");
            }
        });
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}
