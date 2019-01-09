package com.sbs.jamiathcollection.UserInterface;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sbs.jamiathcollection.AppController;
import com.sbs.jamiathcollection.MainActivity;
import com.sbs.jamiathcollection.R;
import com.sbs.jamiathcollection.util.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DonorSMSVerificationScreen extends AppCompatActivity{
    public static final String TAG = "DonorSMSVeriScreen";
    Button btnDonorSMSAddDonor;
    private EditText inputDonorSMSCode;
    private TextInputLayout inputLayoutDonorSMSCode;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout lyt;
    private String phoneNoVerified,donorName,enterdDonorOTP,collectorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_smsverification_screen);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                phoneNoVerified= null;
                donorName=null;
                collectorId=null;
            } else {
                phoneNoVerified= extras.getString("DonorPhone");
                donorName=extras.getString("DonorName");
                collectorId=extras.getString("CollectorId");
            }
        } else {
            phoneNoVerified= (String) savedInstanceState.getSerializable("DonorPhone");
            donorName=(String)savedInstanceState.getSerializable("DonorName");
            collectorId=(String)savedInstanceState.getSerializable("CollectorId");
        }
        Log.d("DonorSMSVerifyScreen","Phone No :"+phoneNoVerified+" Name :"+donorName+" Collector id: "+collectorId);


        lyt=(RelativeLayout)findViewById(R.id.donorSMSLayout);
        inputLayoutDonorSMSCode= (TextInputLayout) findViewById(R.id.input_layout_donorSMSCode);
        inputDonorSMSCode = (EditText) findViewById(R.id.input_donorSMSCode);
        btnDonorSMSAddDonor=(Button)findViewById(R.id.bt_donor_sms_addDonor);
        btnDonorSMSAddDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputDonorSMSCode.getText().toString().isEmpty()){
                    enterdDonorOTP=inputDonorSMSCode.getText().toString();
                    verifyDonor(enterdDonorOTP,phoneNoVerified,donorName,collectorId);
                }else {
                    Toast.makeText(DonorSMSVerificationScreen.this, "Please enter verification code !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyDonor(String dOTP,String dPH,String dNM,String cID){
        JSONObject obj = new JSONObject();
        try{
            obj.put("Name", dNM);
            obj.put("Mobile",dPH);
            obj.put("Code",dOTP);
            obj.put("ID",cID);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/DonorValidate";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        //myJsonReponse=response.toString();

                        try {
                            boolean isSuccess=response.getBoolean("isSuccess");
                            Log.d("Login ","isSuccess"+isSuccess);
                            String data=response.getString("data");
                            //JSONObject data=response.getJSONObject("data");
                            //String donorId=data.getString("id");
                            String message=response.getString("message");

                            if(isSuccess){
                                pDialog.hide();
                                donorVerifySuccess(data);
                            }else{
                                pDialog.hide();
                                donorVerifyFail(message);
                            }
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }

                        pDialog.hide();
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println(error);
                System.out.println(error.getMessage());
                pDialog.hide();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(50000*DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        jsonObjReq.getBody();
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
    private void donorVerifySuccess(String donorId){
        //on success verification
        Intent iDonorSMS=new Intent(DonorSMSVerificationScreen.this,DonorScreen.class);
        iDonorSMS.putExtra("DonorPhone",phoneNoVerified);
        iDonorSMS.putExtra("DonorId",donorId);
        startActivity(iDonorSMS);
        finish();
    }
    private void donorVerifyFail(String msg){
        //on fail verification
        Snackbar snackbar = Snackbar
                .make(lyt, msg, Snackbar.LENGTH_LONG)
                .setAction("RETRY !", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Snackbar snackbar1 = Snackbar.make(lyt, "Message is restored!", Snackbar.LENGTH_SHORT);
                        //snackbar1.show();
                        //Intent intent=new Intent(DonorSMSVerificationScreen.this,AddDonorInfoScreen.class);
                        //startActivity(intent);
                        //finish();
                        if(!inputDonorSMSCode.getText().toString().isEmpty()){
                            enterdDonorOTP=inputDonorSMSCode.getText().toString();
                            verifyDonor(enterdDonorOTP,phoneNoVerified,donorName,collectorId);
                        }else {
                            Toast.makeText(DonorSMSVerificationScreen.this, "Please enter verification code !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        snackbar.show();
    }
}
