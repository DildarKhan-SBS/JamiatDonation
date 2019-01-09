package com.sbs.jamiathcollection.UserInterface;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.sbs.jamiathcollection.util.Donor;
import com.sbs.jamiathcollection.util.DonorAdapter;
import com.sbs.jamiathcollection.util.FamilyMember;
import com.sbs.jamiathcollection.util.MyDividerItemDecoration;
import com.sbs.jamiathcollection.util.MyDonorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonorScreen extends AppCompatActivity {
    public static final String TAG = "DonorScreen";
    Button btnAddFamilyMember,btnReceiveDonation;
    private String phoneNo,donorId,collectorId;
    RelativeLayout lytDonorScreen;
    private TextView tvDonorPhoneNo,tvDonorTotalAmount,tvDonorName;
    //private DonorAdapter mAdapter;
    private MyDonorAdapter myDonorAdapter;
    private List<Donor> dList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText etReceiveDonation,etDonorAmount;
    private double receivedAmount;
    private String[] mDataset=new String[50];
    //private ArrayList memberList=new ArrayList();
    SharedPreferences prefs;
    private List<FamilyMember> familyList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_screen);
        prefs = PreferenceManager
                .getDefaultSharedPreferences(DonorScreen.this);
        collectorId=prefs.getString("collectorId", "0");
        lytDonorScreen=(RelativeLayout)findViewById(R.id.lyt_DonorScreen);
        tvDonorPhoneNo=(TextView)findViewById(R.id.tv_donorPhoneNo);
        tvDonorName=(TextView)findViewById(R.id.tv_donorName);
        etDonorAmount=(EditText)findViewById(R.id.et_donorAmount);
        etReceiveDonation=(EditText)findViewById(R.id.et_totalReceiveDonation);
        btnReceiveDonation=(Button)findViewById(R.id.bt_receiveDonation);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                phoneNo= null;
                donorId= null;
            } else {
                phoneNo= extras.getString("DonorPhone");
                donorId=extras.getString("DonorId");
            }
        } else {
            phoneNo= (String) savedInstanceState.getSerializable("DonorPhone");
            donorId= (String) savedInstanceState.getSerializable("DonorId");
        }
        Log.d("DonorScreen","Phone No :"+phoneNo+" Donor Id: "+donorId);
        tvDonorPhoneNo.setText(phoneNo);

        recyclerView = findViewById(R.id.rv_donorList);
        //mAdapter = new DonorAdapter(this, dList,mDataset);
        myDonorAdapter=new MyDonorAdapter(this,dList,mDataset);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        //recyclerView.setAdapter(mAdapter);
        recyclerView.setAdapter(myDonorAdapter);
        //update ui as per donor details from service call
        getDonorDetails(donorId);

        btnReceiveDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etDonorAmount.getText().toString())){
                    Toast.makeText(DonorScreen.this, "Please enter donor amount", Toast.LENGTH_SHORT).show();
                }else{
                    String donorAmount=etDonorAmount.getText().toString();
                    addDonationAmount(donorAmount);
                }

                /*
                if(receivedAmount<=0){
                    Toast.makeText(DonorScreen.this, "Please check donation amount !", Toast.LENGTH_SHORT).show();
                }else{

                }
                */
            }
        });
        etReceiveDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateValue();
            }
        });

        btnAddFamilyMember=(Button)findViewById(R.id.bt_addFamilyMember);
        btnAddFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFamilyMemberDialog();
            }
        });
    }
    private boolean validateMemberAmount(){
        if(mDataset.length < familyList.size()){
            System.out.println("validateMber: t _"+mDataset.length+"\n"+familyList.size());
            return false;
        }else if(mDataset.length ==  familyList.size()){
            System.out.println("validateMber: F _"+mDataset.length+"\n"+familyList.size());
            return true;
        }else {
            System.out.println("validateMber: F _NA "+mDataset.length+"\n"+familyList.size());
            return false;
        }
        /*
        for(int i=0;i<mDataset.length;i++){
            //if(mDataset[i].equals(null)||mDataset[i].equals("")){
            //  mDataset[i]="0";
            //}
        }*/
    }
    private void getDonorDetails(String id){
        //make service call to get donor details by donor phone number.
        // Tag used to cancel the request

        String tag_json_obj = "json_obj_req";
        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/donor/details/"+id;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            boolean isSuccess = response.getBoolean("isSuccess");
                            String message = response.getString("message");
                            JSONObject data=response.getJSONObject("data");
                            String name = data.getString("name");
                            String mobile = data.getString("mobile");
                            System.out.println(name+" "+mobile);
                            JSONArray relatives=data.getJSONArray("relatives");
                            for(int i=0;i<relatives.length();i++){
                                JSONObject person = (JSONObject) relatives
                                        .get(i);
                                String memberName = person.getString("name");
                                String memberId=person.getString("id");
                                Donor donor=new Donor();
                                donor.setDonorName(memberName);
                                dList.add(donor);

                                FamilyMember member=new FamilyMember();
                                member.setMemberId(memberId);
                                member.setMemberName(memberName);
                                familyList.add(member);
                            }

                            //and finally json array for relatives
                            if(isSuccess){
                                donorDetailsSuccess(name,mobile);
                            }else {
                                donorDetailsFail(message);
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
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void donorDetailsSuccess(String nm,String ph){
        //on success
        tvDonorName.setText(nm);
        //mAdapter.notifyDataSetChanged();
        myDonorAdapter.notifyDataSetChanged();
    }
    private void donorDetailsFail(String msg){
        //on fail
        Snackbar snackbar = Snackbar
                .make(lytDonorScreen, msg, Snackbar.LENGTH_LONG)
                .setAction("RETRY !", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Snackbar snackbar1 = Snackbar.make(lyt, "Message is restored!", Snackbar.LENGTH_SHORT);
                        //snackbar1.show();
                       getDonorDetails(donorId);
                    }
                });

        snackbar.show();
    }

    public void updateValue(){
        double amountOf=0;
        receivedAmount=0;
        for(int i=0;i<myDonorAdapter.getItemCount();i++){
            try{
                amountOf =Double.parseDouble(mDataset[i]);
                receivedAmount=receivedAmount+amountOf;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        try{
            receivedAmount=receivedAmount+Double.parseDouble(etDonorAmount.getText().toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        etReceiveDonation.setText(receivedAmount+"");
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
                        //service call to add Family member to phoneNo
                        addMember(input.getText().toString());
                    }
                });
        alertDialog.show();
    }
    private void addMember(String memberName){
        JSONObject obj = new JSONObject();
        try{
            obj.put("Name", memberName);
            obj.put("PID",donorId);
            obj.put("ID",collectorId);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/donorfamily";

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
                            Log.d("Memebr Added ","isSuccess :"+isSuccess);
                            String data=response.getString("data");
                            String message=response.getString("message");

                            if(isSuccess){
                                pDialog.hide();

                                //refreshing list while getting new member
                                dList.clear();
                                //mDataset=null;
                                familyList.clear();

                                memberAdded();
                            }else{
                                pDialog.hide();
                                memberNotAdded(message);
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
    private void memberAdded(){
        Toast.makeText(DonorScreen.this, "Member Added Successfully !", Toast.LENGTH_SHORT).show();
        //update UI with donor details api call
        dList.clear();
        getDonorDetails(donorId);
    }
    private void memberNotAdded(String msg){
        Toast.makeText(DonorScreen.this, msg+"\nPlease try again !", Toast.LENGTH_SHORT).show();
    }

    private void addDonationAmount(String dAMOUNT){
        //service call for donation
        //System.out.println("Collecor Id: "+collectorId+" DonorId: "+donorId+" Amount: "+receivedAmount);
        JSONObject obj = new JSONObject();
        try{
            obj.put("ID",collectorId);
            obj.put("Amount",dAMOUNT);
            obj.put("DonorID",donorId);
            JSONArray jsonArray=new JSONArray();
            try{
                for(int i=0;i<familyList.size();i++) {
                    FamilyMember member=familyList.get(i);
                    JSONObject object = new JSONObject();
                    object.put("ID", member.getMemberId());
                    if(mDataset[i]==null||mDataset[i]==""){
                        object.put("Amount", "0");
                    }else if(mDataset[i].equals("0")){
                        object.put("Amount","0");
                    }else {
                        object.put("Amount",mDataset[i]);
                    }
                    jsonArray.put(object);
                }
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
            obj.put("Relatives",jsonArray);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        Log.d("Donation object: ",obj.toString());

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/donate";

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
                            Log.d("Donate ","isSuccess :"+isSuccess);
                            //String data=response.getString("data");
                            String message=response.getString("message");

                            if(isSuccess){
                                pDialog.hide();
                                donationSuccess();
                            }else{
                                pDialog.hide();
                                donationFailed(message);
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
                Toast.makeText(DonorScreen.this, "Please enter amount as 0 if no amount !", Toast.LENGTH_LONG).show();
                //mDataset=null;

                pDialog.hide();
                pDialog.dismiss();
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
    private void donationSuccess(){
        Toast.makeText(DonorScreen.this, "Donation Received Successfully !\nThank you.", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(DonorScreen.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void donationFailed(String msg){
        Toast.makeText(DonorScreen.this, msg+"\nPlease try again !", Toast.LENGTH_SHORT).show();
    }

}

