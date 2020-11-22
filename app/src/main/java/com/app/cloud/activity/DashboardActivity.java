package com.app.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.cloud.R;
import com.app.cloud.background.DBAsyncTask;
import com.app.cloud.fragment.NotificationFragment;
import com.app.cloud.listeners.PushDialogListener;
import com.app.cloud.request.Action;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.ApplicationState;
import com.app.cloud.utility.Constants;
import com.app.cloud.utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity implements PushDialogListener {

    private static final String TAG = DashboardActivity.class.getSimpleName();
    String segmentName;
    String message;

    @BindView(R.id.welcome_text)
    TextView welcome;

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        new AppSharedPref(this).putString(Constants.APP_STATE , ApplicationState.SIGNED_IN.toString());
        getFirebaseToken();
        setSupportActionBar(toolbar);

        String name = new AppSharedPref(this).getUser().getName();
        welcome.setText(String.format(getResources().getString(R.string.welcome),name));

        if(getIntent() != null ){
            Intent intent = getIntent();
            if(intent.getAction() != null && intent.getAction().equals(Constants.PUSH)){
                segmentName = intent.getStringExtra(Constants.SEGMENT_NAME);
                message = intent.getStringExtra(Constants.PUSH_MESSAGE);
                showMessageDialog(message,segmentName);
            }
        }

        new DBAsyncTask(this, Action.DBINSERT).execute();
    }

    private void showMessageDialog(String message, String segmentName){
        FragmentManager fm = getSupportFragmentManager();
        NotificationFragment notificationDialog = new NotificationFragment(message,this);
        notificationDialog.show(fm, "fragment_notification_msg");
    }

    private void getFirebaseToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                        new AppSharedPref(DashboardActivity.this).putString(Constants.FCM_TOKEN , token);
                    }
                });
    }

    @OnClick(R.id.logout_btn)
    public void logout(){
        new AppSharedPref(this).clearPref();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void sendInterested() {
        //sendUserActionToServer("Interested");
    }

    @Override
    public void sendNotInterested() {
        //sendUserActionToServer("Not Interested");
    }

    private void sendUserActionToServer(String result) {
        Log.d(TAG, "sendUserActionToServer");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("segment", segmentName);
        jsonObject.addProperty("result", result);

        if (Util.isNetworkAvailable(this)) {

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://www.google.com";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG , "User Action Sent Successfully");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG , "User Action Sent Failed");
                }
            });
            queue.add(stringRequest);
        }
    }
}
