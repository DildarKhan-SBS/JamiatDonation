package com.sbs.jamiathcollection.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sbs.jamiathcollection.R;

public class DonorSMSVerificationScreen extends AppCompatActivity {
Button btnDonorSMSAddDonor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_smsverification_screen);
        btnDonorSMSAddDonor=(Button)findViewById(R.id.bt_donor_sms_addDonor);
        btnDonorSMSAddDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDonorSMS=new Intent(DonorSMSVerificationScreen.this,DonorScreen.class);
                startActivity(iDonorSMS);
            }
        });
    }
}
