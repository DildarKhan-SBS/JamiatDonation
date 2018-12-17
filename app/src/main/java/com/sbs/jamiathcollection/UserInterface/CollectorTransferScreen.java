package com.sbs.jamiathcollection.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.sbs.jamiathcollection.R;

public class CollectorTransferScreen extends AppCompatActivity {
Button btnSearchCollector;
ImageButton ibtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_transfer_screen);
        btnSearchCollector=(Button)findViewById(R.id.bt_searchCollector);
        btnSearchCollector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ibtn=(ImageButton)findViewById(R.id.ib_search);
        ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTransferScreen=new Intent(CollectorTransferScreen.this,TransferScreen.class);
                startActivity(iTransferScreen);
            }
        });
    }
}
