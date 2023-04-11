package com.drhowdydoo.animenews.service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.util.Log;

import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.database.FeedDatabase;

import java.time.LocalDateTime;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class DBCleanupService extends JobService {


    @SuppressLint("CheckResult")
    @Override
    public boolean onStartJob(JobParameters params) {
        FeedDatabase db = FeedDatabase.getInstance(this);
        FeedDao feedDao = db.feedDao();
        SharedPreferences preferences = getSharedPreferences("com.drhowdydoo.preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int limit = preferences.getInt("com.drhowdydoo.settings.articleLimit", 0);
        if (limit == 0) limit = 40;
        else if (limit == 1) limit = 50;
        else limit = 60;

        Log.d("DBCleanUpService", "onStartJob: limit " + limit);
        editor.putString("com.drhowdydoo.service.dbCleanUpTime", LocalDateTime.now().toString()).apply();

        int finalLimit = limit;
        feedDao.getRowCount()
                .subscribeOn(Schedulers.io())
                .subscribe(count -> {
                    Log.d("DBCleanUpService", "onStartJob: count " + count);
                    if (count > finalLimit) {
                        feedDao.deleteOldestRows(finalLimit);
                    }
                });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
