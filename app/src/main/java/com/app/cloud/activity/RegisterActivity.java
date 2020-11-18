package com.app.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.app.cloud.R;
import com.app.cloud.fragment.VerifyCodeFragment;
import com.app.cloud.listeners.DialogListener;
import com.app.cloud.utility.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements DialogListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    CognitoUser cognitoUser;

    @BindView(R.id.name_edit_text)
    EditText name;
    @BindView(R.id.email_edit_text)
    EditText emailAddress;
    @BindView(R.id.phone_edit_text)
    EditText phoneNumber;
    @BindView(R.id.gender_male)
    CheckBox maleGender;
    @BindView(R.id.gender_female)
    CheckBox femaleGender;
    @BindView(R.id.password_edit_text)
    EditText password;
    @BindView(R.id.confirm_pwd_edit_text)
    EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.register_btn)
    public void onClick(){
        String gender;
        if(maleGender.isChecked()){
            gender = maleGender.getText().toString();
        }else gender = femaleGender.getText().toString();
        CognitoUserPool userPool = new CognitoUserPool(this,
                Constants.POOL_ID,
                Constants.APP_CLIENT_ID,
                null ,
                Regions.US_WEST_2);

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        userAttributes.addAttribute("name", name.getText().toString());
        userAttributes.addAttribute("email",emailAddress.getText().toString());
//        userAttributes.addAttribute("phone_number", phoneNumber.getText().toString());
        userAttributes.addAttribute("phone_number", "+01234567890");
        userAttributes.addAttribute("gender", gender);
        userAttributes.addAttribute("birthdate" , "10-15-1988");

        userPool.signUpInBackground(emailAddress.getText().toString(),password.getText().toString(),userAttributes,null,signUpCallback);
    }

    SignUpHandler signUpCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
            Log.d(TAG , "Registration Success. Code sent on email");
            cognitoUser = user;
            showEditDialog();
        }

        @Override
        public void onFailure(Exception exception) {
            Log.d(TAG , "Registration failed: " + exception.getMessage());
        }
    };

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        VerifyCodeFragment verifyDialogFragment = new VerifyCodeFragment(this);
        verifyDialogFragment.show(fm, "fragment_verifyCode");
    }

    @Override
    public void submit(String code) {
        Log.d(TAG , "Code Submit");
        cognitoUser.confirmSignUpInBackground(code,false,confirmationCallback);
    }

    GenericHandler confirmationCallback = new GenericHandler() {
        @Override
        public void onSuccess() {
            Log.d(TAG , "Code Successfully Verified");
            Intent intent = new Intent(RegisterActivity.this,DashboardActivity.class);
            startActivity(intent);
        }

        @Override
        public void onFailure(Exception exception) {
            Log.d(TAG , "Code Verification Failed "+ exception.getMessage());
        }
    };
}
