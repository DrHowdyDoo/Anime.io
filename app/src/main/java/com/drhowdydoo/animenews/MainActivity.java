package com.drhowdydoo.animenews;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.drhowdydoo.animenews.adapter.RecyclerViewAdapter;
import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.database.FeedDatabase;
import com.drhowdydoo.animenews.databinding.ActivityMainBinding;
import com.drhowdydoo.animenews.model.RssItem;
import com.google.android.material.color.DynamicColors;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://animenewsnetwork.com/newsfeed/";
    private static final String TAG = "MainActivity";
    private static FeedDao feedDao;
    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<RssItem> feeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recyclerView;

        feeds = new ArrayList<>();
        adapter = new RecyclerViewAdapter(feeds, MainActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);

        FeedDatabase db = Room.databaseBuilder(getApplicationContext(),FeedDatabase.class,"drhowdydoo-feedDb").build();
        feedDao = db.feedDao();

        RssParser rssParser = new RssParser(this);
        rssParser.getRssFeed(BASE_URL);

    }


    public void updateData(List<RssItem> updatedFeeds){
        feeds.addAll(updatedFeeds);
        adapter.notifyDataSetChanged();
    }

    public RecyclerViewAdapter getAdapter(){
        return adapter;
    }

}