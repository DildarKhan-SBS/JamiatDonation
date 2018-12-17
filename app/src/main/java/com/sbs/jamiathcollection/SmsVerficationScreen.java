package com.sbs.jamiathcollection;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SmsVerficationScreen extends AppCompatActivity {
    private EditText inputVerificationCode;
    private TextInputLayout inputLayoutVerificationCode;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verfication_screen);
        inputLayoutVerificationCode= (TextInputLayout) findViewById(R.id.input_layout_authPhoneNumber);
        inputVerificationCode = (EditText) findViewById(R.id.input_authPhoneNumber);
        btnLogin=(Button)findViewById(R.id.bt_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }
    private void login(){
        Intent iMain=new Intent(SmsVerficationScreen.this,MainActivity.class);
        startActivity(iMain);
    }
}
