package com.sdirin.java.newstracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.sdirin.java.newstracker.adapters.MainAdapter;
import com.sdirin.java.newstracker.data.ApiUtils;
import com.sdirin.java.newstracker.data.database.DatabaseHandler;
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
    NewsResponse newsResponse;
    DatabaseHandler db;

    //testing network support
    private CountingIdlingResource idlingResource;
    private int incrementCalls = 0;
    public boolean isInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = ApiUtils.getService();
        db = new DatabaseHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFromDB();
        displayList(newsResponse);
        getNewsFromNetwork();
    }

    public void getNewsFromNetwork(){
        incrementIdlingResouce();
        mService.getNews().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "loaded network", Toast.LENGTH_SHORT).show();
                    try {
                        newsResponse = NewsServiceParser.fromJson(response.body());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error loading news", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    safeToDb(newsResponse);
                    displayList(newsResponse);
                    decrementIdlingResource();
                } else {
                    int statusCode = response.code();
                    Log.d(TAG, "onResponse: status code = "+statusCode);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showErrorMessage();
                Log.d(TAG, "onResponse: error loading from API");
                decrementIdlingResource();
            }
        });
    }

    private void safeToDb(NewsResponse response) {
        for (int i=0; i<response.getArticles().size(); i++){
            db.addArticle(response.getArticles().get(i));
        }
    }

    private void loadFromDB() {
        newsResponse = new NewsResponse();
        newsResponse.setMessage("ok");
        Toast.makeText(MainActivity.this, "loaded DB", Toast.LENGTH_SHORT).show();
        newsResponse.setArticles(db.getAllArticles());
    }

    private void displayList(NewsResponse response) {
        MainAdapter adapter = new MainAdapter(response);
        RecyclerView list = findViewById(R.id.news_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(adapter);
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
    //testing network support

    public void setCountingIdlingResource(CountingIdlingResource countingIdlingResource) {
        this.idlingResource = countingIdlingResource;
    }
    private void incrementIdlingResouce(){
        if (idlingResource != null){
            idlingResource.increment();
        } else {
            incrementCalls++;
        }
    }
    private void decrementIdlingResource(){
        if (incrementCalls > 0){
            incrementCalls--;
            return;
        }
        if (idlingResource != null){

            idlingResource.decrement();
        }
    }
}
