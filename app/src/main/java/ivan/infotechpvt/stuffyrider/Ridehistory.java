package ivan.infotechpvt.stuffyrider;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ivan.infotechpvt.stuffyrider.adapter.RideUnpaidAdapter;
import ivan.infotechpvt.stuffyrider.adapter.RidehistoryAdapter;
import ivan.infotechpvt.stuffyrider.adapter.RidesearchAdapter;
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.model.RidedetailsModel;
import ivan.infotechpvt.stuffyrider.model.RidehistoryModel;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ridehistory extends AppCompatActivity {

    private static final String TAG = "Myapp";
    ImageView btn_back;
    SessionManager sessionManager;
    String token;
    private static final String SHARED_PREFS = "sharedPrefs";
    private List<RidehistoryModel> ridehistoryModelArrayList;
    private List<RidehistoryModel> rideunpaidArrayList;
    private RidehistoryAdapter ridehistoryAdapter;
    private RideUnpaidAdapter rideUnpaidAdapter;
    private RidesearchAdapter ridesearchAdapter;

    private RecyclerView rv_rideHistroy, rv_inprogress, rv_search;
    TextView btnPaid, btnUnpaid, tvTotalcost, tvNoride;
    View all_underlineBlue, inprogress_underlineBlue, complete_underlineBlue;
    ImageView btnSearch, btnClear;
    String phoneorname;
    EditText etSearch;
    LinearLayout navTab, underlineTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridehistory);
        btnPaid = (TextView) findViewById(R.id.btnPaid);
        btnUnpaid = (TextView) findViewById(R.id.btnUnpaid);
        tvTotalcost = (TextView) findViewById(R.id.tvTotalcost);
        tvNoride = (TextView)findViewById(R.id.tvNoride);
        rv_rideHistroy = (RecyclerView) findViewById(R.id.rv_rideHistroy);
        rv_inprogress = (RecyclerView) findViewById(R.id.rv_inprogress);
        rv_search = findViewById(R.id.rv_search);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        all_underlineBlue = (View) findViewById(R.id.all_underlineBlue);
        inprogress_underlineBlue = (View) findViewById(R.id.inprogress_underlineBlue);
        navTab = findViewById(R.id.navTab);
        underlineTab = findViewById(R.id.underlineTab);

        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        inProgress();

        btn_back = (ImageView) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        btnPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rideHistroy();
                all_underlineBlue.setVisibility(View.INVISIBLE);
                inprogress_underlineBlue.setVisibility(View.VISIBLE);

            }
        });

        btnUnpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inProgress();
                all_underlineBlue.setVisibility(View.VISIBLE);
                inprogress_underlineBlue.setVisibility(View.INVISIBLE);

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etSearch.setText("");
                inProgress();
                navTab.setVisibility(View.VISIBLE);
                underlineTab.setVisibility(View.VISIBLE);


            }
        });


    }


    public void checkblank() {

        phoneorname = etSearch.getText().toString();

        if (phoneorname.length() == 0) {

            Toast.makeText(this, "Please enter a Valid Keyword", Toast.LENGTH_SHORT).show();

        } else {

            searchList();

        }

    }


    public void searchList() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            rv_rideHistroy.setVisibility(View.GONE);
            rv_inprogress.setVisibility(View.GONE);
            tvTotalcost.setVisibility(View.GONE);
            rv_search.setVisibility(View.VISIBLE);
            navTab.setVisibility(View.GONE);
            underlineTab.setVisibility(View.GONE);
            btnClear.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.GONE);

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.RidehistroyUrl,
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
                                    ridehistoryModelArrayList = new ArrayList<>();
                                    JSONObject data = result.getJSONObject("data");
                                    JSONArray ridelistArray = data.getJSONArray("ridelist");
                                    for (int i = 0; i < ridelistArray.length(); i++) {

                                        RidehistoryModel ridehistoryModel = new RidehistoryModel();
                                        JSONObject ridelistobj = ridelistArray.getJSONObject(i);
                                        ridehistoryModel.setId(ridelistobj.getString("id"));
                                        ridehistoryModel.setCustomer_name(ridelistobj.getString("customer_name"));
                                        ridehistoryModel.setCustomer_mobile(ridelistobj.getString("customer_mobile"));
                                        ridehistoryModel.setBooking_no(ridelistobj.getString("booking_no"));
                                        ridehistoryModel.setTotal_ride_time(ridelistobj.getString("total_ride_time"));
                                        ridehistoryModel.setTotal_ride_cost(ridelistobj.getString("total_ride_cost"));
                                        ridehistoryModel.setTotal_ride(ridelistobj.getString("total_ride"));
                                        ridehistoryModel.setStatus(ridelistobj.getString("status"));
                                        ridehistoryModel.setBooking_date(ridelistobj.getString("booking_date"));
                                        ridehistoryModel.setPayment_status(ridelistobj.getString("payment_status"));
                                        ridehistoryModelArrayList.add(ridehistoryModel);

//                                        tvrideNumber.setText(ridelistArray.length()+" Ride Available");

                                    }

                                    setupRecycler4();
                                    ridesearchAdapter.notifyDataSetChanged();
                                    if (ridehistoryModelArrayList.size() > 0) {
                                        tvNoride.setVisibility(View.GONE);
                                    } else {
                                        tvNoride.setVisibility(View.VISIBLE);

                                    }


                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Ridehistory.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ridehistory.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("keyword", phoneorname);


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


    public void rideHistroy() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            rv_rideHistroy.setVisibility(View.VISIBLE);
            rv_inprogress.setVisibility(View.GONE);
            tvTotalcost.setVisibility(View.GONE);
            rv_search.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
            btnSearch.setVisibility(View.VISIBLE);
            navTab.setVisibility(View.VISIBLE);
            underlineTab.setVisibility(View.VISIBLE);


            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.paymentStatusUrl,
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
                                    ridehistoryModelArrayList = new ArrayList<>();
                                    JSONObject data = result.getJSONObject("data");
                                    JSONArray ridelistArray = data.getJSONArray("ridelist");
                                    String today_cost = data.getString("today_cost");
                                    tvTotalcost.setText("$ " + today_cost);
                                    for (int i = 0; i < ridelistArray.length(); i++) {

                                        RidehistoryModel ridehistoryModel = new RidehistoryModel();
                                        JSONObject ridelistobj = ridelistArray.getJSONObject(i);
                                        ridehistoryModel.setId(ridelistobj.getString("id"));
                                        ridehistoryModel.setCustomer_name(ridelistobj.getString("customer_name"));
                                        ridehistoryModel.setCustomer_mobile(ridelistobj.getString("customer_mobile"));
                                        ridehistoryModel.setBooking_no(ridelistobj.getString("booking_no"));
                                        ridehistoryModel.setTotal_ride_time(ridelistobj.getString("total_ride_time"));
                                        ridehistoryModel.setTotal_ride_cost(ridelistobj.getString("total_ride_cost"));
                                        ridehistoryModel.setTotal_ride(ridelistobj.getString("total_ride"));
                                        ridehistoryModel.setStatus(ridelistobj.getString("status"));
                                        ridehistoryModel.setBooking_date(ridelistobj.getString("booking_date"));
                                        ridehistoryModelArrayList.add(ridehistoryModel);

//                                        tvrideNumber.setText(ridelistArray.length()+" Ride Available");

                                    }

                                    setupRecycler();
                                    ridehistoryAdapter.notifyDataSetChanged();
                                    if (ridehistoryModelArrayList.size() > 0) {
                                        tvNoride.setVisibility(View.GONE);
                                    } else {
                                        tvNoride.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Ridehistory.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ridehistory.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("payment_status", "1");

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


    public void inProgress() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            rv_rideHistroy.setVisibility(View.GONE);
            rv_inprogress.setVisibility(View.VISIBLE);
            tvTotalcost.setVisibility(View.GONE);
            rv_search.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
            btnSearch.setVisibility(View.VISIBLE);


            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.paymentStatusUrl,
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
                                    rideunpaidArrayList = new ArrayList<>();
                                    JSONObject data = result.getJSONObject("data");
                                    JSONArray ridelistArray = data.getJSONArray("ridelist");
                                    for (int i = 0; i < ridelistArray.length(); i++) {

                                        RidehistoryModel ridehistoryModel = new RidehistoryModel();
                                        JSONObject ridelistobj = ridelistArray.getJSONObject(i);
                                        ridehistoryModel.setId(ridelistobj.getString("id"));
                                        ridehistoryModel.setCustomer_name(ridelistobj.getString("customer_name"));
                                        ridehistoryModel.setCustomer_mobile(ridelistobj.getString("customer_mobile"));
                                        ridehistoryModel.setBooking_no(ridelistobj.getString("booking_no"));
                                        ridehistoryModel.setTotal_ride_time(ridelistobj.getString("total_ride_time"));
                                        ridehistoryModel.setTotal_ride_cost(ridelistobj.getString("total_ride_cost"));
                                        ridehistoryModel.setTotal_ride(ridelistobj.getString("total_ride"));
                                        ridehistoryModel.setStatus(ridelistobj.getString("status"));
                                        ridehistoryModel.setBooking_date(ridelistobj.getString("booking_date"));
                                        rideunpaidArrayList.add(ridehistoryModel);

//                                        tvrideNumber.setText(ridelistArray.length()+" Ride Available");

                                    }

                                    setupRecycler2();
                                    rideUnpaidAdapter.notifyDataSetChanged();
                                    if (rideunpaidArrayList.size() > 0) {
                                        tvNoride.setVisibility(View.GONE);
                                    } else {
                                        tvNoride.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Ridehistory.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ridehistory.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("payment_status", "0");

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





    public void ridehistorydetails(RidehistoryModel ridehistoryModel,int pos){

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
                                    ArrayList<RidedetailsModel> ridedetailsModelArrayList = new ArrayList<>();
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
                                        ridedetailsModel.setStatus(ridelistobj.getString("status"));
                                        ridedetailsModel.setEnd_time(ridelistobj.getString("end_time"));
                                        ridedetailsModel.setTotal_ridetime(ridelistobj.getString("total_ride_time"));
                                        ridedetailsModelArrayList.add(ridedetailsModel);
                                    }

                                    ridehistoryModel.setDetailslist(ridedetailsModelArrayList);
                                    ridehistoryAdapter.notifyItemChanged(pos);

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Ridehistory.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ridehistory.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("booking_id", ridehistoryModel.getId());

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




    public void ridehistorydetailsUnpaid(RidehistoryModel ridehistoryModel,int pos){

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
                                    ArrayList<RidedetailsModel> ridedetailsModelArrayList = new ArrayList<>();
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
                                        ridedetailsModel.setStatus(ridelistobj.getString("status"));
                                        ridedetailsModel.setTotal_ridetime(ridelistobj.getString("total_ride_time"));
                                        ridedetailsModelArrayList.add(ridedetailsModel);
                                    }

                                    ridehistoryModel.setDetailslist(ridedetailsModelArrayList);
                                    rideUnpaidAdapter.notifyItemChanged(pos);

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Ridehistory.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ridehistory.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("booking_id", ridehistoryModel.getId());

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

        ridehistoryAdapter = new RidehistoryAdapter(this, ridehistoryModelArrayList);
        rv_rideHistroy.setAdapter(ridehistoryAdapter);
        rv_rideHistroy.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void setupRecycler2() {

        rideUnpaidAdapter = new RideUnpaidAdapter(this, rideunpaidArrayList);
        rv_inprogress.setAdapter(rideUnpaidAdapter);
        rv_inprogress.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void setupRecycler4() {

        ridesearchAdapter = new RidesearchAdapter(this, ridehistoryModelArrayList);
        rv_search.setAdapter(ridesearchAdapter);
        rv_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }



    public void paid(RidehistoryModel ridehistoryModel){

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.paidUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {

                                    Toast.makeText(Ridehistory.this, "Booking paid successfully.", Toast.LENGTH_SHORT).show();
                                    // selectedList();
                                    rideunpaidArrayList.remove(ridehistoryModel);
                                    rideUnpaidAdapter.notifyDataSetChanged();
//                                    if (ongoingrideModelArrayList.size() == 0) {
////                                        //visible here .your all ride are cancelled
////                                    }
//                                    if (pendingingrideModelArrayList.size() > 0) {
//                                        tv_noride.setVisibility(View.GONE);
//                                        btnContinue.setVisibility(View.GONE);
//                                    } else {
//                                        tv_noride.setVisibility(View.VISIBLE);
//                                        btnContinue.setVisibility(View.VISIBLE);
//
//                                    }

                                } else {

                                    Toast.makeText(Ridehistory.this, "invalid 1", Toast.LENGTH_SHORT).show();
                                }


                            } catch (Exception e) {
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

                                    Toast.makeText(Ridehistory.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("booking_id", ridehistoryModel.getId());

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




    public void paid2(RidehistoryModel ridehistoryModel){

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.paidUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {

                                    Toast.makeText(Ridehistory.this, "Booking paid successfully.", Toast.LENGTH_SHORT).show();
                                    // selectedList();
                                    ridehistoryModelArrayList.remove(ridehistoryModel);
                                    ridesearchAdapter.notifyDataSetChanged();
//                                    if (ongoingrideModelArrayList.size() == 0) {
////                                        //visible here .your all ride are cancelled
////                                    }
//                                    if (pendingingrideModelArrayList.size() > 0) {
//                                        tv_noride.setVisibility(View.GONE);
//                                        btnContinue.setVisibility(View.GONE);
//                                    } else {
//                                        tv_noride.setVisibility(View.VISIBLE);
//                                        btnContinue.setVisibility(View.VISIBLE);
//
//                                    }

                                } else {

                                    Toast.makeText(Ridehistory.this, "invalid 1", Toast.LENGTH_SHORT).show();
                                }


                            } catch (Exception e) {
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

                                    Toast.makeText(Ridehistory.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("booking_id", ridehistoryModel.getId());

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