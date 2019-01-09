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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.sbs.jamiathcollection.util.Collector;
import com.sbs.jamiathcollection.util.CollectorAdapter;
import com.sbs.jamiathcollection.util.ConnectivityReceiver;
import com.sbs.jamiathcollection.util.MyDividerItemDecoration;
import com.sbs.jamiathcollection.util.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CollectorTransferScreen extends AppCompatActivity implements CollectorAdapter.CollectorAdapterListener{
    private EditText inputPhoneNumber;
    private TextInputLayout inputLayoutPhoneNumber;
    private TextView myCollection;
    private String collectionAmount;
    LinearLayout lytSearchedCollector;
    SharedPreferences prefs;
    private String myCollectorId,myCollectorPhone,receiverCollectorPhone,receiverCollectorId,searchingCollectorPhone;
    Button btn_searchCollector;
    private List<Collector> collectorList=new ArrayList<>();
    private CollectorAdapter mAdapter;
    private RecyclerView rvCollectorList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_transfer_screen);
        //get collector name and phone no from pref then service call to get collection amount from server about that phone no and update ui
        prefs = PreferenceManager
                .getDefaultSharedPreferences(CollectorTransferScreen.this);
        myCollectorId=prefs.getString("collectorId", "0");
        myCollectorPhone=prefs.getString("collectorPhone","0");

        btn_searchCollector=(Button)findViewById(R.id.bt_searchCollector);
        btn_searchCollector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectorList.clear();
                if (!validatePhone()) {
                    return;
                }
                searchingCollectorPhone=inputPhoneNumber.getText().toString();
                searchCollector(searchingCollectorPhone);
            }
        });
        rvCollectorList=(RecyclerView)findViewById(R.id.rv_collectorList);
        rvCollectorList.addOnItemTouchListener(new RecyclerTouchListener(this,
                rvCollectorList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                receiverCollectorPhone=collectorList.get(position).getCollectorPhone();
                receiverCollectorId=collectorList.get(position).getCollectorId();
                transferRequest(receiverCollectorId,receiverCollectorPhone);

                /*
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
            mAdapter = new CollectorAdapter(this, collectorList,this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rvCollectorList.setLayoutManager(mLayoutManager);
            rvCollectorList.setItemAnimator(new DefaultItemAnimator());
            rvCollectorList.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
            rvCollectorList.setAdapter(mAdapter);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        myCollection=(TextView)findViewById(R.id.tv_collectorTransfer_collection);
        inputLayoutPhoneNumber = (TextInputLayout) findViewById(R.id.input_layout_searchCollector);
        inputPhoneNumber = (EditText) findViewById(R.id.input_searchCollector);
        /*
        inputPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //ViewItemScreen.this.adapter.getFilter().filter(cs);
                try {
                    mAdapter.getFilter().filter(cs);
                }
                catch (IndexOutOfBoundsException ex){
                    ex.printStackTrace();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
        */
        //get collection amount details for profile collector id
        getBalanceDetails(myCollectorId);

    }
    private void transferRequest(String receiverId,String receiverPhone){

        JSONObject obj = new JSONObject();
        try{
            obj.put("ID", receiverId);
            obj.put("Mobile",receiverPhone);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/donation/transfer";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CollectorTrScreen ", response.toString());
                        //myJsonReponse=response.toString();
                        try {
                            //String isSuccess=authResponse.getString("isSuccess");
                            boolean isSuccess=response.getBoolean("isSuccess");
                            Log.d("CollectorValidate: ","isSuccess"+isSuccess);
                            String data=response.getString("data");
                            String message=response.getString("message");
                            if(isSuccess){
                                transferRequestSuccess();
                                pDialog.hide();
                            }else{
                                transferRequestFail(message);
                                pDialog.hide();
                            }
                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("CollectorTrScreen", "Error: " + error.getMessage());
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

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void transferRequestSuccess(){
        Intent iTransferScreen = new Intent(CollectorTransferScreen.this, TransferScreen.class);
        iTransferScreen.putExtra("DonorCollectorPhone",myCollectorPhone);
        iTransferScreen.putExtra("DonorCollectorId",myCollectorId);
        iTransferScreen.putExtra("ReceiverCollectorPhone",receiverCollectorPhone);
        iTransferScreen.putExtra("ReceiverCollectorId",receiverCollectorId);
        iTransferScreen.putExtra("CollectionAmount",collectionAmount);
        startActivity(iTransferScreen);
    }
    private void transferRequestFail(String msg){
        Toast.makeText(CollectorTransferScreen.this, msg+"\nPlease try again.", Toast.LENGTH_SHORT).show();

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
                        Log.d("ColTransefer ", response.toString());
                        try {
                            boolean isSuccess = response.getBoolean("isSuccess");
                            String data=response.getString("data");
                            String message = response.getString("message");

                            //and finally json array for relatives
                            if(isSuccess){
                                balanceAmountSuccess(data);
                                collectionAmount=data;
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
                Log.d("CollectorTrnSc: ",error.getMessage());
                Toast.makeText(CollectorTransferScreen.this, "Service unavailable !\nPlease try later.", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                pDialog.hide();
                pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void searchCollector(String phone) {
        //service call to search donor with phone
        String tag_json_obj = "json_obj_req";

        String url = "http://ec2-34-209-27-165.us-west-2.compute.amazonaws.com:888/api/Collector/"+phone;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ColTransefer ", response.toString());
                        try {
                            boolean isSuccess = response.getBoolean("isSuccess");
                            String message = response.getString("message");
                            JSONArray data = response.getJSONArray("data");
                            String id=null,name=null,mobile=null;
                            for(int i=0;i<data.length();i++){
                                JSONObject donorInfo = (JSONObject) data.get(i);
                                id=donorInfo.getString("id");
                                name = donorInfo.getString("name");
                                mobile = donorInfo.getString("mobile");
                                Collector collector=new Collector();
                                collector.setCollectorId(id);
                                collector.setCollectorName(name);
                                collector.setCollectorPhone(mobile);
                                collectorList.add(collector);
                            }
                            //and finally json array for relatives
                            if(isSuccess){
                                collectorFoundSuccess(collectorList);
                            }else {
                                collectorFoundFail(message);
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
                Log.d("CollectorTrnSc: ",error.getMessage());
                Toast.makeText(CollectorTransferScreen.this, "Service unavailable !\nPlease try later.", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                pDialog.hide();
                pDialog.dismiss();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void collectorFoundSuccess(List<Collector>  cList){
        Log.d("Clist size",""+cList.size());
        if(cList.size()<1){
            Toast.makeText(CollectorTransferScreen.this, "Collector not exist !", Toast.LENGTH_LONG).show();
        }
        mAdapter.notifyDataSetChanged();
    }
    private void collectorFoundFail(String msg){
        //on fail
        Toast.makeText(CollectorTransferScreen.this, msg+"\nPlease try again !", Toast.LENGTH_SHORT).show();
        lytSearchedCollector.setVisibility(View.GONE);
    }
    private void balanceAmountSuccess(String amount){
        myCollection.setText("My Collection "+amount);
    }
    private void balanceAmountFail(String msg){
        myCollection.setText(msg);
        Toast.makeText(CollectorTransferScreen.this, "Service unavailable"+"\n"+msg, Toast.LENGTH_SHORT).show();
    }
    private boolean validatePhone() {
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
    public void onItemSelected(Collector item) {
        //Toast.makeText(getApplicationContext(), "Selected: " + .getName() + ", " + item.getPrice(), Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Selected: ", Toast.LENGTH_LONG).show();

    }

}
