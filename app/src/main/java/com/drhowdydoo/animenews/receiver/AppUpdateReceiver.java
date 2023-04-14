package com.drhowdydoo.animenews.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.drhowdydoo.animenews.util.DBCleanupScheduler;

public class AppUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
                new DBCleanupScheduler().schedule(context.getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

