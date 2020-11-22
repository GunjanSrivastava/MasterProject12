package com.app.cloud.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.cloud.database.DatabaseAccess;
import com.app.cloud.request.Action;

public class DBAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = DBAsyncTask.class.getSimpleName();
    private Context context;
    private Action action;

    public DBAsyncTask(Context context , Action action){
        this.context = context;
        this.action = action;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d(TAG , "Executing doInBackground...");
        boolean isSuccess = false;
        DatabaseAccess.getInstance(context).dbInteraction(action);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
    }

}
