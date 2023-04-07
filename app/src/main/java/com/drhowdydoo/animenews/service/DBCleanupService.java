package com.drhowdydoo.animenews.service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;

import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.database.FeedDatabase;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class DBCleanupService extends JobService {


    @SuppressLint("CheckResult")
    @Override
    public boolean onStartJob(JobParameters params) {
         FeedDatabase db = FeedDatabase.getInstance(this);
         FeedDao feedDao = db.feedDao();

         feedDao.getRowCount()
                 .subscribeOn(Schedulers.io())
                 .subscribe(count -> {
                     if (count > 50) {
                         feedDao.deleteOldestRows();
                     }
                 });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
