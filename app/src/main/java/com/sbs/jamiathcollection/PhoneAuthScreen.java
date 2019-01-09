package com.sbs.jamiathcollection;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PhoneAuthScreen extends AppCompatActivity {
    public static final String TAG = "PhoneAuthScreen";
    private EditText inputPhoneNumber;
    private TextInputLayout inputLayoutPhoneNumber;
    private Button btnSendCode;
    private String PHONE;
    private String phoneOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth_screen);
        inputLayoutPhoneNumber= (TextInputLayout) findViewById(R.id.input_layout_authPhoneNumber);
        inputPhoneNumber = (EditText) findViewById(R.id.input_authPhoneNumber);
        btnSendCode=(Button)findViewById(R.id.bt_phoneAuthSendCode);
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePhone()) {
                    return;
                }
                //startPhoneVerification();
                PHONE=inputPhoneNumber.getText().toString();
                //String isSuccess=null,data,message;
                verifyPhoneNo(PHONE);
            }
        });

    }

    public void loginFail(){
        Toast.makeText(PhoneAuthScreen.this, "Login Fail", Toast.LENGTH_SHORT).show();
    }
    public void loginSuccess(){
        //make service call to send phone number for verification

        //on success start to next screen.
        Intent iVerificationScreen=new Intent(PhoneAuthScreen.this,SmsVerficationScreen.class);
        iVerificationScreen.putExtra("phoneNumber",inputPhoneNumber.getText().toString());
        startActivity(iVerificationScreen);
        finish();
    }

    private void verifyPhoneNo(String phNo){

        JSONObject obj = new JSONObject();
        try{
            obj.put("Mobile", phNo);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/account/collector";

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
                            phoneOTP=data;
                            String message=response.getString("message");

                            if(isSuccess){
                                pDialog.hide();
                                loginSuccess();
                            }else{
                                pDialog.hide();
                                loginFail();
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
    }

    private boolean validatePhone(){
        String phoneNo = inputPhoneNumber.getText().toString().trim();

        if (phoneNo.isEmpty() || !isValidPhone(phoneNo)) {
            inputLayoutPhoneNumber.setError("Invalid Phone Number !");
            requestFocus(inputPhoneNumber);
            return false;
        } else {
            inputLayoutPhoneNumber.setErrorEnabled(false);
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
