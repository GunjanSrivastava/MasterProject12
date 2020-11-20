package com.app.cloud.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.cloud.activity.DashboardActivity;
import com.app.cloud.utility.Constants;

public class IncomingNotificationService extends Service {

    private static final String TAG = IncomingNotificationService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG , "onStartCommand " + intent);
        String msg = "";
        String segment = "";
        if(intent != null){
            msg = intent.getStringExtra(Constants.PUSH_MESSAGE);
            segment = intent.getStringExtra(Constants.SEGMENT_NAME);
            Intent activityIntent = new Intent(this, DashboardActivity.class);
            activityIntent.setAction(intent.getAction());
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.putExtra(Constants.PUSH_MESSAGE , msg);
            activityIntent.putExtra(Constants.SEGMENT_NAME , segment);
            startActivity(activityIntent);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
