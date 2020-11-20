package com.app.cloud.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cloud.database.DatabaseAccess;

public class DBAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = DBAsyncTask.class.getSimpleName();
    private Context context;

    public DBAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d(TAG , "Executing doInBackground...");
        boolean isSuccess = false;
        DatabaseAccess dbAccess = DatabaseAccess.getInstance(context);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
    }

}
