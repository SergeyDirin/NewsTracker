package com.sdirin.java.newstracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.sdirin.java.newstracker.adapters.MainAdapter;
import com.sdirin.java.newstracker.data.ApiUtils;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.network.NewsService;
import com.sdirin.java.newstracker.data.parse.NewsServiceParser;

import java.text.ParseException;

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

        mService = ApiUtils.getService();

        mService.getNews().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "loaded", Toast.LENGTH_SHORT).show();
                    displayList(response.body());
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

    private void displayList(String json) {
        try {
            NewsResponse response = NewsServiceParser.fromJson(json);

            MainAdapter adapter = new MainAdapter(response);
            RecyclerView list = findViewById(R.id.news_list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            list.setLayoutManager(layoutManager);
            list.setItemAnimator(new DefaultItemAnimator());
            list.setAdapter(adapter);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Parse error", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}
