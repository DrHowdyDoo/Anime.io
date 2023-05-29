package com.drhowdydoo.animenews;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
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
import com.drhowdydoo.animenews.util.DBCleanupScheduler;
import com.drhowdydoo.animenews.util.MyDiffUtilCallback;
import com.drhowdydoo.animenews.util.RssParser;
import com.google.android.material.chip.Chip;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://animenewsnetwork.com/newsfeed/";
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearProgressIndicator syncIndicator;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<RssItem> feeds;
    private CompositeDisposable disposables;
    private FeedDao feedDao;
    private boolean scrollToTop = false;
    private List<RssItem> storedFeeds = new ArrayList<>();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        swipeRefreshLayout = binding.swipeRefresh;
        syncIndicator = binding.syncIndicator;
        recyclerView = binding.recyclerView;

        int progressBackgroundColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorBackgroundFloating, Color.WHITE);
        int progressIndicatorColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.BLACK);

        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(progressBackgroundColor);
        swipeRefreshLayout.setColorSchemeColors(progressIndicatorColor);

        SharedPreferences preferences = getSharedPreferences("com.drhowdydoo.preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int cornerRadius = preferences.getInt("com.drhowdydoo.settings.cornerRadius",50);

        feeds = new ArrayList<>();
        adapter = new RecyclerViewAdapter(feeds, MainActivity.this,cornerRadius);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        new DBCleanupScheduler().schedule(this);


        FeedDatabase db = Room.databaseBuilder(getApplicationContext(), FeedDatabase.class, "drhowdydoo-feedDb").build();
        feedDao = db.feedDao();

        RssParser rssParser = new RssParser(this, feedDao);

        if (preferences.getBoolean("com.drhowdydoo.settings.syncOnStart", false)) {
            syncIndicator.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setEnabled(false);
            rssParser.getRssFeed(BASE_URL);
        }

        Set<String> filterSet = preferences.getStringSet("com.drhowdydoo.filters", null);
        disposables = new CompositeDisposable();

        if (filterSet != null && !filterSet.isEmpty()) {
            getFilteredFeeds(filterSet);
        } else {
            getFeeds();
        }


        if (preferences.getBoolean("firstLaunch", true)) {
            Log.d(TAG, "onCreate: FirstLaunch");
            binding.feedPlaceholder.setVisibility(View.VISIBLE);
            rssParser.getRssFeed(BASE_URL);
            editor.putBoolean("firstLaunch", false).apply();
        }


        swipeRefreshLayout.setOnRefreshListener(() -> {
            syncIndicator.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            scrollToTop = true;
            rssParser.getRssFeed(BASE_URL);
        });

        swipeRefreshLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);



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
                            editor.putStringSet("com.drhowdydoo.filters", filters).apply();
                            editor.putStringSet("com.drhowdydoo.checkedFilters", filterChipIds).apply();
                            if (filters.isEmpty()) {
                                scrollToTop = true;
                                getFeeds();
                            } else {
                                getFilteredFeeds(filters);
                            }
                        })
                        .setNegativeButton("Reset",((dialog, which) -> {
                            editor.putStringSet("com.drhowdydoo.filters",null ).apply();
                            editor.putStringSet("com.drhowdydoo.checkedFilters", null).apply();
                            scrollToTop = true;
                            getFeeds();
                        }))
                        .show();
                Set<String> checkedFilterChips = preferences.getStringSet("com.drhowdydoo.checkedFilters", null);
                if (checkedFilterChips != null) {
                    checkedFilterChips.forEach(id -> {
                        Chip chip = filterDialogBinding.getRoot().findViewById(Integer.parseInt(id));
                        chip.setChecked(true);
                    });
                }
                return true;
            } else if (item.getItemId() == R.id.settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (item.getItemId() == R.id.search) {
                if (binding.searchBar.isShown()) {
                    binding.searchBar.setVisibility(View.GONE);
                }else {
                    binding.searchBar.setVisibility(View.VISIBLE);
                    binding.searchInput.requestFocus();
                    imm.showSoftInput(binding.searchInput,InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
            } else {
                return false;
            }
        });


        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterFeeds(s.toString());
            }
        });

        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                imm.hideSoftInputFromWindow(binding.searchInput.getWindowToken(),0);
                return true;
            }
            return false;
        });

        binding.scrollBack.setOnClickListener(v -> {
            recyclerView.smoothScrollToPosition(0);
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    binding.scrollBack.show();
                }
                else if (dy < 0) {
                    binding.scrollBack.hide();
                }
            }
        });

    }

    private void filterFeeds(String text) {
        if (!text.isEmpty()) {
            List<RssItem> results;
            results = storedFeeds.stream().filter(item -> item.getTitle().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
            if (results.isEmpty()) {
                binding.emptyPlaceholder.setVisibility(View.VISIBLE);
                binding.txtEmptyTitle.setText("No results found");
                binding.txtEmptyBody.setVisibility(View.INVISIBLE);
            } else {
                binding.emptyPlaceholder.setVisibility(View.GONE);
            }
            updateData(results);
            return;
        }
        binding.emptyPlaceholder.setVisibility(View.GONE);
        updateData(storedFeeds);
    }


    public void updateData(List<RssItem> updatedFeeds) {

        MyDiffUtilCallback diffCallback = new MyDiffUtilCallback(feeds, updatedFeeds);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        feeds.clear();
        feeds.addAll(updatedFeeds);
        diffResult.dispatchUpdatesTo(adapter);
        if (scrollToTop) recyclerView.post(() -> recyclerView.smoothScrollToPosition(0));
        scrollToTop = false;
    }

    public void getFilteredFeeds(Set<String> filterSet) {
        disposables.clear();
        disposables.add(feedDao.getAllFeeds(filterSet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    storedFeeds.clear();
                    storedFeeds.addAll(response);
                    if (response.isEmpty()) {
                        binding.emptyPlaceholder.setVisibility(View.VISIBLE);
                    } else binding.emptyPlaceholder.setVisibility(View.GONE);
                    updateData(response);

                }));
    }

    public void getFeeds() {
        disposables.clear();
        disposables.add(feedDao.getAllFeeds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    storedFeeds.clear();
                    storedFeeds.addAll(response);
                    binding.emptyPlaceholder.setVisibility(View.GONE);
                    if (binding.feedPlaceholder.isShown() && !response.isEmpty()) {
                        binding.feedPlaceholder.setVisibility(View.GONE);
                    }
                    updateData(response);

                }));
    }

    public void stopRefreshing() {
        swipeRefreshLayout.setEnabled(true);
        syncIndicator.setVisibility(View.GONE);
    }

    public void showError(String msg) {
        Snackbar.make(binding.getRoot(), msg, BaseTransientBottomBar.LENGTH_SHORT)
                .show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


}