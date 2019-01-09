package com.sbs.jamiathcollection.UserInterface;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class AddDonorInfoScreen extends AppCompatActivity{
    public static final String TAG = "AddDonorInfoScreen";
Button btnProceed;
    private EditText inputDonorPhoneNumber,inputDonorName;
    private TextInputLayout inputLayoutDonorPhoneNumber,inputLayoutDonorName;
    private String donorPhoneNo,donorName,donorOTP;
    SharedPreferences prefs;
    private String collectorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_donor_info_screen);
        prefs = PreferenceManager
                .getDefaultSharedPreferences(AddDonorInfoScreen.this);
        collectorId=prefs.getString("collectorId", "0");
        inputLayoutDonorPhoneNumber= (TextInputLayout) findViewById(R.id.input_layout_donorPhoneNumber);
        inputDonorPhoneNumber = (EditText) findViewById(R.id.input_donorPhoneNumber);
        inputLayoutDonorName= (TextInputLayout) findViewById(R.id.input_layout_donorName);
        inputDonorName = (EditText) findViewById(R.id.input_donorName);
        btnProceed=(Button)findViewById(R.id.bt_addDonor_proceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check validate
                if (!validatePhone()) {
                    return;
                }
                if (!validateName()) {
                    return;
                }
                donorName=inputDonorName.getText().toString();
                donorPhoneNo=inputDonorPhoneNumber.getText().toString();
                addDonorDetails(donorPhoneNo,donorName);
            }
        });
    }

    public void addDonorDetails(String dPH,String dNM){
        JSONObject obj = new JSONObject();
        try{
            obj.put("Name", dNM);
            obj.put("Mobile",dPH);
            obj.put("ID",collectorId);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/Donor";

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
                            //String isSuccess=authResponse.getString("isSuccess");
                            boolean isSuccess=response.getBoolean("isSuccess");
                            Log.d("Login ","isSuccess"+isSuccess);
                            String data=response.getString("data");
                            donorOTP=data;
                            String message=response.getString("message");

                            if(isSuccess){
                                pDialog.hide();
                                donorAddSuccess();
                            }else{
                                pDialog.hide();
                                donorAddFail(message);
                            }
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }

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
        //DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //jsonObjReq.setRetryPolicy(retryPolicy);

        //queue.add(request);

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);



        /*

        //on success response
        Intent iDonorSMS=new Intent(AddDonorInfoScreen.this,DonorSMSVerificationScreen.class);
        iDonorSMS.putExtra("DonorPhone",donorPhoneNo);
        System.out.println(donorPhoneNo);
        startActivity(iDonorSMS);
        finish();

        //cross check with donor already exist or not {if yes goto SEARCH DONOR screen}
        */
    }
    private void donorAddSuccess(){
        Intent iDonorSMS=new Intent(AddDonorInfoScreen.this,DonorSMSVerificationScreen.class);
        iDonorSMS.putExtra("DonorPhone",donorPhoneNo);
        iDonorSMS.putExtra("DonorName",donorName);
        iDonorSMS.putExtra("CollectorId",collectorId);
        System.out.println(donorPhoneNo+" "+donorName);
        startActivity(iDonorSMS);
        finish();

    }
    private void donorAddFail(String msg){
        Toast.makeText(AddDonorInfoScreen.this, msg+"\nPlease try again !", Toast.LENGTH_SHORT).show();
        //on fail response please retry
        //Intent iDonorSMS=new Intent(AddDonorInfoScreen.this,AddDonorInfoScreen.class);
        //startActivity(iDonorSMS);
        //finish();
    }

    private boolean validatePhone(){
        String phoneNo = inputDonorPhoneNumber.getText().toString().trim();

        if (phoneNo.isEmpty() || !isValidPhone(phoneNo)) {
            inputLayoutDonorPhoneNumber.setError("Invalid Phone Number !");
            requestFocus(inputDonorPhoneNumber);
            return false;
        } else {
            inputLayoutDonorPhoneNumber.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateName(){
        String name = inputDonorName.getText().toString().trim();

        if (name.isEmpty()) {
            inputLayoutDonorName.setError("Enter Name !");
            requestFocus(inputDonorName);
            return false;
        } else {
            inputLayoutDonorName.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
