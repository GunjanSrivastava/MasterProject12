package com.app.cloud.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPref {
    private static final String APP_PREF = "app_pref";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public  AppSharedPref(Context context){
        this.context = context;
        this.sharedpreferences = context.getSharedPreferences(APP_PREF,
                Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void putString(String key , String value){
        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key){
        return sharedpreferences.getString(key , ApplicationState.SIGNED_OUT.toString());
    }

    public void clearPref(){
        editor.clear();
        editor.commit();
    }
}
