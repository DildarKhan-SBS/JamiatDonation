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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sbs.jamiathcollection.AppController;
import com.sbs.jamiathcollection.MainActivity;
import com.sbs.jamiathcollection.PhoneAuthScreen;
import com.sbs.jamiathcollection.R;
import com.sbs.jamiathcollection.util.Collector;
import com.sbs.jamiathcollection.util.CollectorAdapter;
import com.sbs.jamiathcollection.util.ConnectivityReceiver;
import com.sbs.jamiathcollection.util.Donor;
import com.sbs.jamiathcollection.util.DonorAdapter;
import com.sbs.jamiathcollection.util.MyDividerItemDecoration;
import com.sbs.jamiathcollection.util.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchDonorScreen extends AppCompatActivity implements DonorAdapter.DonorAdapterListener {
    public static final String TAG = "SearchDonorScreen";
    Button btnSearchDonor;
    private EditText inputPhoneNumber;
    private TextInputLayout inputLayoutPhoneNumber;

//LinearLayout lytSearchedDonor;
//TextView tvSearchedName,tvSearchedPhone;
//ImageButton ibDonorScreen;
private String actualDonorPhoneNo="",donorPhoneNo,donorId,collectorId;
    SharedPreferences prefs;
    private List<Donor> donorList=new ArrayList<>();
    private DonorAdapter mAdapter;
    private RecyclerView rvDonorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_donor_screen);
        prefs = PreferenceManager
                .getDefaultSharedPreferences(SearchDonorScreen.this);
        collectorId= prefs.getString("collectorId","0");

        inputLayoutPhoneNumber= (TextInputLayout) findViewById(R.id.input_layout_searchDonor);
        inputPhoneNumber = (EditText) findViewById(R.id.input_searchDonor);
        /*
        lytSearchedDonor=(LinearLayout)findViewById(R.id.lyt_donorSearch_searchedDonor);
        tvSearchedName=(TextView)findViewById(R.id.tv_donorSearch_name);
        tvSearchedPhone=(TextView)findViewById(R.id.tv_donorSearch_phoneNo);
        ibDonorScreen=(ImageButton)findViewById(R.id.ib_donorSearch_donorScreen);
        ibDonorScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //donorId="13";
                Intent iDonorScreen=new Intent(SearchDonorScreen.this,DonorScreen.class);
                iDonorScreen.putExtra("DonorPhone",actualDonorPhoneNo);
                iDonorScreen.putExtra("DonorId",donorId);
                startActivity(iDonorScreen);
            }
        });
        */

        rvDonorList=(RecyclerView)findViewById(R.id.rv_donorList);
        rvDonorList.addOnItemTouchListener(new RecyclerTouchListener(this,
                rvDonorList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                actualDonorPhoneNo=donorList.get(position).getDonorPhone();
                donorId=donorList.get(position).getDonorId();
                Intent iDonorScreen=new Intent(SearchDonorScreen.this,DonorScreen.class);
                iDonorScreen.putExtra("DonorPhone",actualDonorPhoneNo);
                iDonorScreen.putExtra("DonorId",donorId);
                startActivity(iDonorScreen);
               /*
                receiverCollectorPhone=collectorList.get(position).getCollectorPhone();
                receiverCollectorId=collectorList.get(position).getCollectorId();
                Intent iTransferScreen = new Intent(CollectorTransferScreen.this, TransferScreen.class);
                iTransferScreen.putExtra("DonorCollectorPhone",myCollectorPhone);
                iTransferScreen.putExtra("DonorCollectorId",myCollectorId);
                iTransferScreen.putExtra("ReceiverCollectorPhone",receiverCollectorPhone);
                iTransferScreen.putExtra("ReceiverCollectorId",receiverCollectorId);
                iTransferScreen.putExtra("CollectionAmount",collectionAmount);
                startActivity(iTransferScreen);
                */
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        try {
            mAdapter = new DonorAdapter(this, donorList,this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rvDonorList.setLayoutManager(mLayoutManager);
            rvDonorList.setItemAnimator(new DefaultItemAnimator());
            rvDonorList.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
            rvDonorList.setAdapter(mAdapter);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        btnSearchDonor=(Button)findViewById(R.id.bt_searchDonor);
        btnSearchDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePhone()) {
                    return;
                }
                donorPhoneNo=inputPhoneNumber.getText().toString();
                searchDonor(collectorId,donorPhoneNo);
            }
        });
    }

    private void searchDonor(String collectorId, String phone){
        //service call to search donor with donorPhone and collector id
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/donor/"+collectorId+"/"+phone;

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
                            try {
                                JSONArray data = response.getJSONArray("data");
                                String id = null, name = null, mobile = null;
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject donorInfo = (JSONObject) data.get(i);
                                    id = donorInfo.getString("id");
                                    name = donorInfo.getString("name");
                                    mobile = donorInfo.getString("mobile");
                                    actualDonorPhoneNo = mobile;

                                    Donor donor = new Donor();
                                    donor.setDonorName(name);
                                    donor.setDonorPhone(mobile);
                                    donor.setDonorId(id);
                                    donorList.add(donor);
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                            //and finally json array for relatives
                            if(isSuccess/* && !TextUtils.isEmpty(id)*/){
                                donorFoundSuccess(donorList);
                            }else {
                                donorFoundFail(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
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
    private void donorFoundSuccess(List<Donor> dList){
        //on success
        Log.d("Clist size",""+dList.size());
        if(dList.size()<1){
            Toast.makeText(SearchDonorScreen.this, "Donor not exist !", Toast.LENGTH_LONG).show();
        }
        mAdapter.notifyDataSetChanged();
    }
    private void donorFoundFail(String msg){
        //on fail
        Toast.makeText(SearchDonorScreen.this, msg+"\nPlease try again !", Toast.LENGTH_LONG).show();
        //lytSearchedDonor.setVisibility(View.GONE);
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

    @Override
    public void onItemSelected(Donor donor) {

    }

}
