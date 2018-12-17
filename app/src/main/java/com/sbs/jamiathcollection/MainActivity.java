package com.sbs.jamiathcollection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sbs.jamiathcollection.UserInterface.AddDonorInfoScreen;
import com.sbs.jamiathcollection.UserInterface.NotificationScreen;
import com.sbs.jamiathcollection.UserInterface.SearchDonorScreen;
import com.sbs.jamiathcollection.UserInterface.TransferMainScreen;

public class MainActivity extends AppCompatActivity {
Button  btnAddDonor,btnSearchDonor,btnTransfer,btnNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddDonor=(Button)findViewById(R.id.bt_addDonor);
        btnSearchDonor=(Button)findViewById(R.id.bt_searchDonor);
        btnTransfer=(Button)findViewById(R.id.bt_transfer);
        btnNotification=(Button)findViewById(R.id.bt_notification);
        btnAddDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iAddDonorScreen=new Intent(MainActivity.this,AddDonorInfoScreen.class);
                startActivity(iAddDonorScreen);
            }
        });
        btnSearchDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSearchDonor=new Intent(MainActivity.this,SearchDonorScreen.class);
                startActivity(iSearchDonor);
            }
        });
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Comming soon... !", Toast.LENGTH_SHORT).show();
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTransfer=new Intent(MainActivity.this,TransferMainScreen.class);
                startActivity(iTransfer);
            }
        });
    }
}
