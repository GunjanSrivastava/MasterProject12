package com.app.cloud.activity;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.app.cloud.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyActivity extends AppCompatActivity {
    @BindView(R.id.verify_edit_text)
    EditText verificationCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_verify);
        ButterKnife.bind(this);
        CognitoUser user = (CognitoUser) getIntent().getParcelableExtra("cognitoUser");
    }

    @OnClick(R.id.submit_btn)
    public void submitCode(){

    }

    GenericHandler confirmationCallback = new GenericHandler() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure(Exception exception) {
        }
    };
}
