package com.sbs.jamiathcollection;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sbs.jamiathcollection.util.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SmsVerficationScreen extends AppCompatActivity {
    public static final String TAG = "SmsVerificationScreen";
    private EditText inputVerificationCode;
    private TextInputLayout inputLayoutVerificationCode;
    private Button btnLogin;
    private String phoneNo,enteredOTP,collectorId;
    private TextView tvResend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verfication_screen);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                phoneNo= null;
            } else {
                phoneNo= extras.getString("phoneNumber");
            }
        } else {
            phoneNo= (String) savedInstanceState.getSerializable("phoneNumber");
        }

        Log.d("SmsVerificationScreen","Phone No IS:"+phoneNo);


        inputLayoutVerificationCode= (TextInputLayout) findViewById(R.id.input_layout_verificationCode);
        inputVerificationCode = (EditText) findViewById(R.id.input_verificationCode);
        tvResend=(TextView)findViewById(R.id.tvResendCode);
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setEnabled(false);
                inputVerificationCode.setEnabled(false);
                resendCode(phoneNo);
            }
        });
        btnLogin=(Button)findViewById(R.id.bt_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateCode()) {
                    return;
                }
                enteredOTP=inputVerificationCode.getText().toString();
                verifyOTP(phoneNo,enteredOTP);
                //login();
            }
        });

    }

    public void verifyOTP(String ph,String otp){
        JSONObject obj = new JSONObject();
        try{
            obj.put("Mobile", ph);
            obj.put("Code",otp);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/AccountValidate";

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
                            Log.d("SmsVerification ","isSuccess :"+isSuccess);
                            JSONObject data=response.getJSONObject("data");
                            String id=data.getString("id");
                            collectorId=id;
                            String message=response.getString("message");

                            if(isSuccess){
                                pDialog.hide();
                                smsVerificationSuccess(id);
                            }else{
                                pDialog.hide();
                                smsVerificationFail(message);
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

    private void smsVerificationSuccess(String c_id){
        //on success (sharedPref firstTime-TRUE, Go to MAIN ACTIVITY)
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SmsVerficationScreen.this);//context.getSharedPreferences("LibProfile",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("firstTime", true);
        editor.putString("collectorId",c_id);
        editor.putString("collectorPhone",phoneNo);
        editor.commit();
        Intent iMain = new Intent(SmsVerficationScreen.this, MainActivity.class);
        startActivity(iMain);
        finish();
    }
    private void smsVerificationFail(String msg){
        //on fail (GO to PhoneAuthScreen or RESEND button)
        Toast.makeText(SmsVerficationScreen.this, msg+"\nPlease try again !", Toast.LENGTH_SHORT).show();
        tvResend.setVisibility(View.VISIBLE);
        inputVerificationCode.setText("");
        //btnLogin.setEnabled(false);
        //inputVerificationCode.setEnabled(false);
    }

    public void resendCode(String phone){
        //make service call to resend otp to phone number


        //on success otp send enable inputVerificationCode and Login Button
        inputVerificationCode.setEnabled(true);
        btnLogin.setEnabled(true);
        tvResend.setVisibility(View.GONE);

    }

    private boolean validateCode(){
        String code = inputVerificationCode.getText().toString().trim();
        if (code.isEmpty() || !isValidCode(code)) {
            inputLayoutVerificationCode.setError("Invalid Code !");
            requestFocus(inputVerificationCode);
            return false;
        } else {
            inputLayoutVerificationCode.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidCode(String code) {
        return !TextUtils.isEmpty(code) && code.length()>=4;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
