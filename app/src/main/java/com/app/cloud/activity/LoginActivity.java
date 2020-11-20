package com.app.cloud.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.regions.Regions;
import com.app.cloud.R;
import com.app.cloud.fragment.ErrorHandlerFragment;
import com.app.cloud.request.UserCognitoSessionToken;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.Constants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;

import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName() ;
    CognitoUser cognitoUser;
    @BindView(R.id.email_edit_text)
    EditText emailAddress;
    @BindView(R.id.password_edit_text)
    EditText password;
    String pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if(getIntent() != null){
            if(getIntent().getStringExtra(Constants.USER_ID) != null){
                emailAddress.setText(getIntent().getStringExtra(Constants.USER_ID));
            }
        }
    }

    @OnClick(R.id.login_btn)
    public void onClick(){
        String email = emailAddress.getText().toString();
        pwd = password.getText().toString();
        CognitoUserPool userPool = new CognitoUserPool(this,
                Constants.POOL_ID,
                Constants.APP_CLIENT_ID,
                null ,
                Regions.US_WEST_2);

        cognitoUser = userPool.getUser(email);

        cognitoUser.getSessionInBackground(authenticationHandler);
    }

    @OnClick(R.id.signUp_btn)
    public void signUp(){
        Log.d(TAG , "SignUp");
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            Log.d(TAG, "Login Success");
            UserCognitoSessionToken cognitoSession = new UserCognitoSessionToken(userSession.getAccessToken(),userSession.getIdToken(),userSession.getRefreshToken(),
                    userSession.getUsername());
            new AppSharedPref(LoginActivity.this).putUserSession(cognitoSession);

            Intent intent = new Intent(LoginActivity.this , DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, pwd, null);

            authenticationContinuation.setAuthenticationDetails(authenticationDetails);

            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }

        @Override
        public void onFailure(Exception exception) {
            Log.d(TAG, "Login Failed: " + exception.getMessage());
            FragmentManager fm = getSupportFragmentManager();
            ErrorHandlerFragment errorDialogFragment = new ErrorHandlerFragment(exception.getMessage());
            errorDialogFragment.show(fm, Constants.ERROR_DIALOG_FRAGMENT);
        }
    };
}