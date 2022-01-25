package ivan.infotechpvt.stuffyrider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import ivan.infotechpvt.stuffyrider.R;

import ivan.infotechpvt.stuffyrider.adapter.RidedetailsAdapter;
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.model.RidedetailsModel;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ridehistorydetails extends AppCompatActivity {

    String booking_id, cName, cMobile, totalrideCost, tvTotalridetimeandNumber ;
    TextView customerName, customerNumber, tvSubtotal, tvTimeridetotal, tvNetamount;
    ImageView btn_back;
    String token;
    private static final String TAG = "Myapp";
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    private ArrayList<RidedetailsModel> ridedetailsModelArrayList = new ArrayList<>();
    RidedetailsAdapter ridedetailsAdapter;
    RecyclerView rvride_details;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridehistorydetails);
        customerName = findViewById(R.id.customerName);
        customerNumber = findViewById(R.id.customerNumber);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTimeridetotal = findViewById(R.id.tvTimeridetotal);
        tvNetamount = findViewById(R.id.tvNetamount);
        rvride_details = findViewById(R.id.rvride_details);
        btn_back = findViewById(R.id.btn_back);

        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");


        Intent intent = getIntent();
        booking_id = intent.getStringExtra("booking_id");
        cName = intent.getStringExtra("customerName");
        cMobile = intent.getStringExtra("mobileNumber");
        totalrideCost = intent.getStringExtra("totalrideCost");
        tvTotalridetimeandNumber = intent.getStringExtra("tvTotalridetimeandNumber");

        customerName.setText(cName);
        customerNumber.setText("Mobile No. "+cMobile);
        tvSubtotal.setText("$"+totalrideCost);
        tvTimeridetotal.setText(tvTotalridetimeandNumber);
        tvNetamount.setText("$"+totalrideCost);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        ridedetailsList();


    }


    public void ridedetailsList(){


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.ridedetailsUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                Log.d(TAG, "msg-->" + msg);
                                boolean status = result.getBoolean("status");
                                if (status) {
                                    JSONObject data = result.getJSONObject("data");
                                    JSONArray ridelistArray = data.getJSONArray("ridelist");
                                    for (int i = 0; i < ridelistArray.length(); i++) {

                                        RidedetailsModel ridedetailsModel = new RidedetailsModel();
                                        JSONObject ridelistobj = ridelistArray.getJSONObject(i);
                                        ridedetailsModel.setId(ridelistobj.getString("id"));
                                        ridedetailsModel.setRide_name(ridelistobj.getString("ride_name"));
                                        ridedetailsModel.setMin_ride_time(ridelistobj.getString("min_ride_time"));
                                        ridedetailsModel.setMin_ride_price(ridelistobj.getString("min_ride_price"));
                                        ridedetailsModel.setAddition_ride_time(ridelistobj.getString("addition_ride_time"));
                                        ridedetailsModel.setAddition_ride_cost(ridelistobj.getString("addition_ride_cost"));
                                        ridedetailsModel.setTotal_ride_cost(ridelistobj.getString("total_ride_cost"));
                                        ridedetailsModel.setStatr_time(ridelistobj.getString("start_time"));
                                        ridedetailsModel.setEnd_time(ridelistobj.getString("end_time"));
                                        ridedetailsModel.setTotal_ridetime(ridelistobj.getString("total_ride_time"));

                                        ridedetailsModelArrayList.add(ridedetailsModel);

//                                        tvrideNumber.setText(ridelistArray.length()+" Ride Available");

                                    }

                                    setupRecycler();

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Ridehistorydetails.this, "invalid", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            hideProgressDialog();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                                    Toast.makeText(Ridehistorydetails.this, res, Toast.LENGTH_SHORT).show();

                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                } catch (UnsupportedEncodingException e1) {
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                } catch (JSONException e2) {
                                    // returned data is not JSONObject?
                                    e2.printStackTrace();
                                }
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("booking_id", booking_id);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", token);
                    return params;
                }

            };

            Volley.newRequestQueue(this).add(stringRequest);


        } else {

            Toast.makeText(getApplicationContext(), "OOPS! No Internet Connection", Toast.LENGTH_SHORT).show();

        }


    }


    private void setupRecycler() {

        ridedetailsAdapter = new RidedetailsAdapter(this, ridedetailsModelArrayList);
        rvride_details.setAdapter(ridedetailsAdapter);
        rvride_details.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


    }


    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}