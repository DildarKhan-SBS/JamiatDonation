package com.sbs.jamiathcollection.UserInterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sbs.jamiathcollection.R;

public class SearchDonorScreen extends AppCompatActivity {
Button btnSearchDonor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_donor_screen);
        btnSearchDonor=(Button)findViewById(R.id.bt_searchDonor);
        btnSearchDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}
