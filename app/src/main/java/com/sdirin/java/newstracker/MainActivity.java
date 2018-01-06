package com.sdirin.java.newstracker;

import android.os.Bundle;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.sdirin.java.newstracker.adapters.MainAdapter;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.database.DatabaseHandler;
import com.sdirin.java.newstracker.presenters.MainPresenter;
import com.sdirin.java.newstracker.view.MainScreen;

public class MainActivity extends AppCompatActivity implements MainScreen {

    private static final String TAG = "NewsApp";
    NewsResponse newsResponse;
    MainPresenter presenter;

    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.onResume();
        displayList();
    }

    public MainPresenter getPresenter(){
        return presenter;
    }

    public void setNewsResponse(NewsResponse newsResponse){
        if (this.newsResponse == null){
            this.newsResponse = new NewsResponse();
        }
        this.newsResponse.combineWith(newsResponse);
    }

    public void displayList() {
        if (newsResponse == null) {
            return;
        }
        RecyclerView list = findViewById(R.id.news_list);
        if (adapter == null){
            adapter = new MainAdapter(newsResponse);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            list.setLayoutManager(layoutManager);
            list.setItemAnimator(new DefaultItemAnimator());
            list.setAdapter(adapter);
        } else {
            list.invalidate();
        }

    }

    @Override
    public DatabaseHandler getDb() {
        return new DatabaseHandler(this);
    }

    public void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    public void setCountingIdlingResource(CountingIdlingResource countingIdlingResource) {
        presenter.setCountingIdlingResource(countingIdlingResource);
    }
}
