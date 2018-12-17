package com.sbs.jamiathcollection.UserInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sbs.jamiathcollection.MainActivity;
import com.sbs.jamiathcollection.R;

public class TransferScreen extends AppCompatActivity {
Button btTransferSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_screen);
        btTransferSubmit=(Button)findViewById(R.id.bt_transferSubmit);
        btTransferSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TransferScreen.this, "Thanks you !", Toast.LENGTH_SHORT).show();
                Intent iTransferDone=new Intent(TransferScreen.this,MainActivity.class);
                startActivity(iTransferDone);
            }
        });
    }
}
