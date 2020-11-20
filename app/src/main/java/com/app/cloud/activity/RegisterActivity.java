package com.app.cloud.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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
import com.app.cloud.fragment.DatePickerFragment;
import com.app.cloud.fragment.ErrorHandlerFragment;
import com.app.cloud.fragment.VerifyCodeFragment;
import com.app.cloud.listeners.DialogListener;
import com.app.cloud.request.User;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements DialogListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    CognitoUser cognitoUser;
    User userData;

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
    @BindView(R.id.dob_edit_text)
    TextView selectedDOB;
    @BindView(R.id.age_edit_text)
    EditText age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.register_btn)
    public void onClick(){
        String userName = name.getText().toString();
        String email = emailAddress.getText().toString();
        String phone = "+0" + phoneNumber.getText().toString();
        String dob = selectedDOB.getText().toString();
        String userAge = age.getText().toString();
        String gender;

        if(maleGender.isChecked()){
            gender = maleGender.getText().toString();
        }else gender = femaleGender.getText().toString();

        userData = new User(userName,email,phone,dob,userAge,gender);

        CognitoUserPool userPool = new CognitoUserPool(this,
                Constants.POOL_ID,
                Constants.APP_CLIENT_ID,
                null ,
                Regions.US_WEST_2);

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        userAttributes.addAttribute("name", userName);
        userAttributes.addAttribute("email",email);
        userAttributes.addAttribute("phone_number", phone);
        userAttributes.addAttribute("gender", gender);
        userAttributes.addAttribute("birthdate" , dob);

        userPool.signUpInBackground(email,password.getText().toString(),userAttributes,null,signUpCallback);
    }

    SignUpHandler signUpCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
            Log.d(TAG , "Registration Success. Code sent on email");
            new AppSharedPref(RegisterActivity.this).putUser(userData);
            cognitoUser = user;
            showVerifyCodeDialog();
        }

        @Override
        public void onFailure(Exception exception) {
            Log.d(TAG , "Registration failed: " + exception.getMessage());
            showErrorDialog(exception.getMessage());
        }
    };

    GenericHandler confirmationCallback = new GenericHandler() {
        @Override
        public void onSuccess() {
            Log.d(TAG , "Code Successfully Verified");
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            intent.putExtra(Constants.USER_ID , cognitoUser.getUserId());
            startActivity(intent);
        }

        @Override
        public void onFailure(Exception exception) {
            Log.d(TAG , "Code Verification Failed "+ exception.getMessage());
            showErrorDialog(exception.getMessage());
        }
    };

    @Override
    public void submit(String code) {
        Log.d(TAG , "Code Submit");
        cognitoUser.confirmSignUpInBackground(code,false,confirmationCallback);
    }

    @Override
    public void setDate(String date) {
        selectedDOB.setText(date);
    }

    @OnClick({R.id.select_dob_view , R.id.dob_edit_text})
    public void selectDate(){
        FragmentManager fm = getSupportFragmentManager();
        DatePickerFragment datePickerDialogFragment = new DatePickerFragment(this);
        datePickerDialogFragment.show(fm, "fragment_verifyCode");
    }

    private void showVerifyCodeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        VerifyCodeFragment verifyDialogFragment = new VerifyCodeFragment(this);
        verifyDialogFragment.show(fm, "fragment_verifyCode");
    }

    private void showErrorDialog(String msg){
        FragmentManager fm = getSupportFragmentManager();
        ErrorHandlerFragment errorHandlerFragment = new ErrorHandlerFragment(msg);
        errorHandlerFragment.show(fm, Constants.ERROR_DIALOG_FRAGMENT);
    }
}
