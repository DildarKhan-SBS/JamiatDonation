package com.sbs.jamiathcollection.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sbs.jamiathcollection.R;

public class AddDonorInfoScreen extends AppCompatActivity {
Button btnProceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donor_info_screen);
        btnProceed=(Button)findViewById(R.id.bt_addDonor_proceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDonorSMS=new Intent(AddDonorInfoScreen.this,DonorSMSVerificationScreen.class);
                startActivity(iDonorSMS);

            }
        });
    }
}
