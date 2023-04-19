package com.drhowdydoo.animenews.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import com.drhowdydoo.animenews.service.DBCleanupService;

public class DBCleanupScheduler {


    public void schedule(Context context){
        SharedPreferences preferences = context.getSharedPreferences("com.drhowdydoo.preferences", context.MODE_PRIVATE);
        int timePeriodId = preferences.getInt("com.drhowdydoo.settings.cleanupTime",0);
        long timePeriod = timePeriodId == 0 ? (24 * 60 * 60 * 1000L) : timePeriodId == 1 ? (24 * 60 * 60 * 1000L * 3) : (24 * 60 * 60 * 1000L * 7);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo DBCleanupJob = jobScheduler.getPendingJob(1);
        if (DBCleanupJob != null) return;
        ComponentName componentName = new ComponentName(context, DBCleanupService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(timePeriod)
                .setPersisted(true)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    public void reschedule(Context context, long timePeriod){

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, DBCleanupService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(timePeriod)
                .setPersisted(true)
                .build();
        jobScheduler.schedule(jobInfo);
    }

}
