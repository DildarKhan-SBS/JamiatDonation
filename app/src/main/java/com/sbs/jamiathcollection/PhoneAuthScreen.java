package com.sbs.jamiathcollection;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class PhoneAuthScreen extends AppCompatActivity {
    private EditText inputPhoneNumber;
    private TextInputLayout inputLayoutPhoneNumber;
    private Button btnSendCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth_screen);
        inputLayoutPhoneNumber= (TextInputLayout) findViewById(R.id.input_layout_authPhoneNumber);
        inputPhoneNumber = (EditText) findViewById(R.id.input_authPhoneNumber);
        btnSendCode=(Button)findViewById(R.id.bt_phoneAuthSendCode);
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoneVerification();
            }
        });

    }

    public void startPhoneVerification(){
        if (!validatePhone()) {
            return;
        }
        Intent iVerificationScreen=new Intent(PhoneAuthScreen.this,SmsVerficationScreen.class);
        startActivity(iVerificationScreen);
    }

    private boolean validatePhone(){
        String phoneNo = inputPhoneNumber.getText().toString().trim();

        if (phoneNo.isEmpty() || !isValidPhone(phoneNo)) {
            inputLayoutPhoneNumber.setError("Invalid Phone Number !");
            requestFocus(inputPhoneNumber);
            return false;
        } else {
            inputLayoutPhoneNumber.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
