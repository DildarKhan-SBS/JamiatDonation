package com.sbs.jamiathcollection.UserInterface;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sbs.jamiathcollection.R;

public class DonorScreen extends AppCompatActivity {
Button btnAddFamilyMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_screen);
        btnAddFamilyMember=(Button)findViewById(R.id.bt_addFamilyMember);
        btnAddFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFamilyMemberDialog();
            }
        });


    }
    public void btnAddFamilyMemberDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DonorScreen.this);
        alertDialog.setTitle("Add Family Member");
        //alertDialog.setMessage("Donor Family Member Name");

        final EditText input = new EditText(DonorScreen.this);
        input.setHint("Donor Family Member Name");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertDialog.setView(input);



        alertDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(DonorScreen.this, "Name : "+input.getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                });



        alertDialog.show();
    }

}

