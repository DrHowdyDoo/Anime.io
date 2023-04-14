package com.drhowdydoo.animenews.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.drhowdydoo.animenews.service.DBCleanupService;

public class DBCleanupScheduler {


    public void schedule(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo DBCleanupJob = jobScheduler.getPendingJob(1);
        if (DBCleanupJob != null) return;
        ComponentName componentName = new ComponentName(context, DBCleanupService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(24 * 60 * 60 * 1000L) // run once per day
                .setPersisted(true)
                .build();
        jobScheduler.schedule(jobInfo);
    }

}
