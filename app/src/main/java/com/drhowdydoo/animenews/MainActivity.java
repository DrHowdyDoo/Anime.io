package com.drhowdydoo.animenews;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.drhowdydoo.animenews.adapter.RecyclerViewAdapter;
import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.database.FeedDatabase;
import com.drhowdydoo.animenews.databinding.ActivityMainBinding;
import com.drhowdydoo.animenews.databinding.FilterDialogBinding;
import com.drhowdydoo.animenews.model.RssItem;
import com.drhowdydoo.animenews.network.RssParser;
import com.drhowdydoo.animenews.service.DBCleanupService;
import com.drhowdydoo.animenews.util.MyDiffUtilCallback;
import com.google.android.material.chip.Chip;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://animenewsnetwork.com/newsfeed/";
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<RssItem> feeds;
    private CompositeDisposable disposables;
    private FeedDao feedDao;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        swipeRefreshLayout = binding.swipeRefresh;
        recyclerView = binding.recyclerView;

        int progressBackgroundColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorBackgroundFloating, Color.WHITE);
        int progressIndicatorColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.BLACK);

        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(progressBackgroundColor);
        swipeRefreshLayout.setColorSchemeColors(progressIndicatorColor);


        feeds = new ArrayList<>();
        adapter = new RecyclerViewAdapter(feeds, MainActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences("com.drhowdydoo.preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        FeedDatabase db = Room.databaseBuilder(getApplicationContext(), FeedDatabase.class, "drhowdydoo-feedDb").build();
        feedDao = db.feedDao();

        RssParser rssParser = new RssParser(this, feedDao);

        Set<String> filterSet = preferences.getStringSet("com.drhowdydoo.filters",null);
        disposables = new CompositeDisposable();

        if (filterSet!= null) {
            applyFilters(filterSet);
        } else {
            disposables.clear();
            disposables.add(feedDao.getAllFeeds()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (!response.isEmpty()) {
                            updateData(response);
                        }
                    }));
        }




        if (preferences.getBoolean("firstLaunch", true)) {
            binding.feedPlaceholder.setVisibility(View.VISIBLE);
            rssParser.getRssFeed(BASE_URL);
            editor.putBoolean("firstLaunch", false).apply();
        }


        swipeRefreshLayout.setOnRefreshListener(() -> rssParser.getRssFeed(BASE_URL));

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this, DBCleanupService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(24 * 60 * 60 * 1000L) // run once per day
                .build();
        jobScheduler.schedule(jobInfo);



        binding.materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.filter) {
                FilterDialogBinding filterDialogBinding = FilterDialogBinding.inflate(LayoutInflater.from(this));
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                materialAlertDialogBuilder
                        .setTitle("Apply Filters")
                        .setView(filterDialogBinding.getRoot())
                        .setPositiveButton("Apply", (dialog, which) -> {
                            Set<String> filters = new HashSet<>();
                            Set<String> filterChipIds = new HashSet<>();
                            List<Integer> checkIds = filterDialogBinding.chipGroup.getCheckedChipIds();
                            checkIds.forEach(id -> {
                                Chip chip = filterDialogBinding.getRoot().findViewById(id);
                                filters.add(chip.getText().toString());
                                filterChipIds.add(String.valueOf(id));
                            });
                            editor.putStringSet("com.drhowdydoo.filters",filters).apply();
                            editor.putStringSet("com.drhowdydoo.checkedFilters",filterChipIds).apply();
                            applyFilters(filters);
                        }).show();
                Set<String> checkedFilterChips = preferences.getStringSet("com.drhowdydoo.checkedFilters",null);
                if (checkedFilterChips != null) {
                    checkedFilterChips.forEach(id -> {
                        Chip chip = filterDialogBinding.getRoot().findViewById(Integer.parseInt(id));
                        chip.setChecked(true);
                    });
                }
                return true;
            } else {
                return false;
            }
        });

    }


    public void updateData(List<RssItem> updatedFeeds) {
        if (binding.feedPlaceholder.isShown()) {
            binding.feedPlaceholder.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(false);
        MyDiffUtilCallback diffCallback = new MyDiffUtilCallback(feeds, updatedFeeds);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        feeds.clear();
        feeds.addAll(updatedFeeds);
        diffResult.dispatchUpdatesTo(adapter);
        recyclerView.smoothScrollToPosition(0);
    }

    public void applyFilters(Set<String> filterSet){
        disposables.clear();
        disposables.add(feedDao.getAllFeeds(filterSet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (!response.isEmpty()) {
                        updateData(response);
                    }
                }));
    }

    public RecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public void stopRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void showError(String msg) {
        Snackbar.make(binding.getRoot(), msg, BaseTransientBottomBar.LENGTH_SHORT)
                .show();
    }

}