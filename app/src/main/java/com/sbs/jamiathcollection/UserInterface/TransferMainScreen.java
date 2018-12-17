package com.sbs.jamiathcollection.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sbs.jamiathcollection.R;

public class TransferMainScreen extends AppCompatActivity {
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
