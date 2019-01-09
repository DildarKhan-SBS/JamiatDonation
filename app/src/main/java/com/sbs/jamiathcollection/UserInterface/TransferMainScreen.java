package com.sbs.jamiathcollection.UserInterface;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sbs.jamiathcollection.AppController;
import com.sbs.jamiathcollection.MainActivity;
import com.sbs.jamiathcollection.R;
import com.sbs.jamiathcollection.util.ConnectivityReceiver;

public class TransferMainScreen extends AppCompatActivity{
Button btnTransferToMarkz,btnTransferToCollector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_main_screen);
        btnTransferToMarkz=(Button)findViewById(R.id.bt_transferToMarkz);
        btnTransferToCollector=(Button)findViewById(R.id.bt_transferToCollector);
        btnTransferToCollector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTransferToCollector=new Intent(TransferMainScreen.this,CollectorTransferScreen.class);
                startActivity(iTransferToCollector);
            }
        });
    }


}
