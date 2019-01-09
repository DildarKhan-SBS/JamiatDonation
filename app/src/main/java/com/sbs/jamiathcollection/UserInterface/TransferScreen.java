package com.sbs.jamiathcollection.UserInterface;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.sbs.jamiathcollection.AppController;
import com.sbs.jamiathcollection.MainActivity;
import com.sbs.jamiathcollection.R;
import com.sbs.jamiathcollection.util.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransferScreen extends AppCompatActivity{
Button btTransferSubmit;
    private EditText inputTransferOTP;
    private TextInputLayout inputLayoutTransferOTP;
    private TextView tvTransferSender,tvTransferReceiver,tvTransferAmount;
    private String donorCollectorPhone,donorCollectorId,receiverCollectorPhone,receiverCollectorId,collectionAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_screen);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                donorCollectorPhone= null;
                donorCollectorId= null;
                receiverCollectorPhone= null;
                receiverCollectorId=null;
                collectionAmount= null;
            } else {
                donorCollectorPhone= extras.getString("DonorCollectorPhone");
                donorCollectorId= extras.getString("DonorCollectorId");
                receiverCollectorPhone=extras.getString("ReceiverCollectorPhone");
                receiverCollectorId=extras.getString("ReceiverCollectorId");
                collectionAmount=extras.getString("CollectionAmount");
            }
        } else {
            donorCollectorPhone= (String) savedInstanceState.getSerializable("DonorCollectorPhone");
            donorCollectorId= (String) savedInstanceState.getSerializable("DonorCollectorId");
            receiverCollectorPhone= (String) savedInstanceState.getSerializable("ReceiverCollectorPhone");
            receiverCollectorId= (String) savedInstanceState.getSerializable("ReceiverCollectorId");
            collectionAmount=(String)savedInstanceState.getSerializable("CollectionAmount");
        }

        inputLayoutTransferOTP= (TextInputLayout) findViewById(R.id.input_layout_transferOTP);
        inputTransferOTP = (EditText) findViewById(R.id.input_transferOTP);
        tvTransferSender=(TextView)findViewById(R.id.tv_transfer_senderCollectorName);
        tvTransferReceiver=(TextView)findViewById(R.id.tv_transfer_receiverName);
        tvTransferAmount=(TextView)findViewById(R.id.tv_transfer_amount);
        //make service call to update ui
        updateUI();
        btTransferSubmit=(Button)findViewById(R.id.bt_transferSubmit);
        btTransferSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputTransferOTP.getText().toString().isEmpty()){
                    Toast.makeText(TransferScreen.this, "Please input transfer OTP !", Toast.LENGTH_LONG).show();
                }else{
                    String otp=inputTransferOTP.getText().toString();
                    transferAmount(otp);
                }
            }
        });
    }
    private void updateUI(){
        //service call includes [get sender name from pref phone no] [get receiver name from service by providing putExtra phone no] [get transfer amount from service call by pref phone no]
        tvTransferSender.setText("Sender: "+donorCollectorPhone);
        tvTransferReceiver.setText("Receiver: "+receiverCollectorPhone);
        tvTransferAmount.setText("Transferring Amount: "+collectionAmount);

    }

    private void transferAmount(String otp){
        //make service call to transfer amount
        JSONObject obj = new JSONObject();
        try{
            obj.put("ID", receiverCollectorId);
            obj.put("Amount",collectionAmount);
            obj.put("DonorID",donorCollectorId);
            obj.put("Code",otp);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/donation/transfer/validation";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Transfer Screen: ", response.toString());
                        //myJsonReponse=response.toString();
                        try {
                            //String isSuccess=authResponse.getString("isSuccess");
                            boolean isSuccess=response.getBoolean("isSuccess");
                            Log.d("Login ","isSuccess"+isSuccess);
                            String data=response.getString("data");
                            //donorOTP=data;
                            String message=response.getString("message");

                            if(isSuccess){
                                pDialog.hide();
                                transferSuccess(message);
                            }else{
                                pDialog.hide();
                                transferFail(message);
                            }
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }

                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Transfer Screen: ", "Error: " + error.getMessage());
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
    private void transferSuccess(String msg){
        //on success
        Toast.makeText(TransferScreen.this, ""+msg, Toast.LENGTH_SHORT).show();
        Intent iTransferDone=new Intent(TransferScreen.this,MainActivity.class);
        startActivity(iTransferDone);
    }
    private void transferFail(String msg){
        //on fail
        Toast.makeText(TransferScreen.this, msg+"\nPlease try again !", Toast.LENGTH_SHORT).show();
    }
}


/*
dList.clear();
        mDataset=null;
        familyList.clear();
        getDonorDetails(donorId);
 */