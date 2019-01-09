package com.sbs.jamiathcollection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sbs.jamiathcollection.UserInterface.AddDonorInfoScreen;
import com.sbs.jamiathcollection.UserInterface.NotificationScreen;
import com.sbs.jamiathcollection.UserInterface.SearchDonorScreen;
import com.sbs.jamiathcollection.UserInterface.TransferMainScreen;
import com.sbs.jamiathcollection.util.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    public static final String TAG = "MainActivity";
    Button  btnAddDonor,btnSearchDonor,btnTransfer,btnNotification;
    SharedPreferences prefs;
    private String collectorId;
    TextView tvCollectionAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        if (!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            Intent iFirst = new Intent(MainActivity.this, PhoneAuthScreen.class);
            startActivity(iFirst);
            finish();
        } else {


            setContentView(R.layout.activity_main);

            collectorId = prefs.getString("collectorId", "0");

            tvCollectionAmount = (TextView) findViewById(R.id.tv_MainScreenCollectionAmount);
            btnAddDonor = (Button) findViewById(R.id.bt_addDonor);
            btnSearchDonor = (Button) findViewById(R.id.bt_searchDonor);
            btnTransfer = (Button) findViewById(R.id.bt_transfer);
            btnNotification = (Button) findViewById(R.id.bt_notification);
            btnAddDonor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iAddDonorScreen = new Intent(MainActivity.this, AddDonorInfoScreen.class);
                    startActivity(iAddDonorScreen);
                }
            });
            btnSearchDonor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iSearchDonor = new Intent(MainActivity.this, SearchDonorScreen.class);
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
                    Intent iTransfer = new Intent(MainActivity.this, TransferMainScreen.class);
                    startActivity(iTransfer);
                }
            });
            //getBalanceDetails(collectorId);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(new ConnectivityReceiver(), intentFilter);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logOut(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);//context.getSharedPreferences("LibProfile",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();
        //Intent iMain = new Intent(MainActivity.this, MainActivity.class);
        //startActivity(iMain);
        finish();
        //clear all activities

    }
    private void getBalanceDetails(String collectorId){
        //service call to get collection amount
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/collector/balance/"+collectorId;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Main Screen ", response.toString());
                        try {
                            boolean isSuccess = response.getBoolean("isSuccess");
                            String data=response.getString("data");
                            String message = response.getString("message");

                            //and finally json array for relatives
                            if(isSuccess){
                                balanceAmountSuccess(data);
                            }else {
                                balanceAmountFail(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        pDialog.hide();
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("CollTransfer ", "Error: " + error.getMessage());
                //Log.d("CollectorTrnSc: ",error.getMessage());
                //Toast.makeText(MainActivity.this, "Service unavailable !\nPlease try later.", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                pDialog.hide();
                pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void balanceAmountSuccess(String amount){
        tvCollectionAmount.setText("My Collection "+amount);
    }
    private void balanceAmountFail(String msg){
        tvCollectionAmount.setText(msg);
        Toast.makeText(MainActivity.this, "Service unavailable"+"\n"+msg, Toast.LENGTH_SHORT).show();
    }
    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }
    @Override
    public void onResume(){
        super.onResume();
        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);

        prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        collectorId = prefs.getString("collectorId", "0");
        getBalanceDetails(collectorId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(new ConnectivityReceiver());
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
    private void showSnack(boolean isConnected){
        String message;
        int color;
        if (isConnected) {
            message = "Internet available !";
            color = Color.WHITE;
        } else {
            message = "Sorry ! No connection";
            color = Color.RED;
        }
        Snackbar.make(findViewById(R.id.lyt_main_activity), message, Snackbar.LENGTH_LONG).show();
    }
}