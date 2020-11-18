package com.app.cloud.utility;

import android.content.Context;
import android.content.Intent;

import com.app.cloud.activity.DashboardActivity;
import com.app.cloud.activity.LoginActivity;

public class Util {

    public static Intent getIntent(Context context){
        String appState = new AppSharedPref(context).getString(Constants.APP_STATE);
        if(appState.equals(ApplicationState.SIGNED_IN.toString())){
            return new Intent(context, DashboardActivity.class);
        }else {
            return new Intent(context, LoginActivity.class);
        }
    }
}
