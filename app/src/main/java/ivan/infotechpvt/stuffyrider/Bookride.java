package ivan.infotechpvt.stuffyrider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ivan.infotechpvt.stuffyrider.adapter.RideOngoingAdapter;
import ivan.infotechpvt.stuffyrider.adapter.RidePendingAdapter;
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.model.OngoingrideModel;
import ivan.infotechpvt.stuffyrider.model.TimeSlotModel;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

import static ivan.infotechpvt.stuffyrider.Ongoingdetails.needToRefresh;

public class Bookride extends AppCompatActivity {

    ImageView btn_back;
    String token, id;
    private static final String TAG = "Myapp";
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    private ArrayList<OngoingrideModel> ongoingrideModelArrayList = new ArrayList<>();
    private ArrayList<OngoingrideModel> pendingingrideModelArrayList = new ArrayList<>();
    RideOngoingAdapter rideOngoingAdapter;
    RidePendingAdapter ridePendingAdapter;
    RecyclerView rv_ongoingride, rv_pendingride;
    TextView tv_noride, tvOngoing, tvPending;
    LinearLayout btnContinue;
    private String ridebasetime = "";
    public String ridebasecharge = "";
    LinearLayout btnRideAvailable, btnPendingrides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookride);
        rv_ongoingride = (RecyclerView) findViewById(R.id.rv_ongoingride);
        rv_pendingride = (RecyclerView) findViewById(R.id.rv_pendingride);
        tv_noride = findViewById(R.id.tv_noride);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btnContinue = (LinearLayout) findViewById(R.id.btnContinue);
        btnRideAvailable = (LinearLayout) findViewById(R.id.btnRideAvailable);
        sessionManager = new SessionManager(getApplicationContext());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        setupOngoingRecycler();
        setupPendingRecycler();
        ongoingList();
        // pendingList();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Bookride.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Bookride.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needToRefresh) {
            needToRefresh = false;
            ongoingList();
        }
    }


    public void stopallride(OngoingrideModel ongoingrideModel) {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.stopallride,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));
                            Intent intent = new Intent(Bookride.this, Bookride.class);
                            startActivity(intent);

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {

                                    Toast.makeText(Bookride.this, "All started ride completed successfully", Toast.LENGTH_SHORT).show();


                                } else {

                                    Toast.makeText(Bookride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("booking_id", ongoingrideModel.getBooking_id());

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

    //Ongoing

    public void ongoingList() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, AllUrl.customerList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("ResponseList-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                Log.d(TAG, "msg-->" + msg);
                                boolean status = result.getBoolean("status");
                                if (status) {
                                    ongoingrideModelArrayList.clear();
                                    JSONArray data = result.getJSONArray("data");
                                    for (int j = 0; j < data.length(); j++) {

                                        JSONObject custo = data.getJSONObject(j);
                                        OngoingrideModel ongoingrideModelcustomer = new OngoingrideModel();
                                        ongoingrideModelcustomer.setBooking_id(custo.getString("booking_id"));
                                        ongoingrideModelcustomer.setCustomer_name(custo.getString("customer_name"));
                                        ongoingrideModelcustomer.setCustomer_mobile(custo.getString("customer_mobile"));
                                        ongoingrideModelArrayList.add(ongoingrideModelcustomer);
                                        JSONArray ridelistArray = custo.getJSONArray("ridelist");
                                        for (int i = 0; i < ridelistArray.length(); i++) {

                                            OngoingrideModel ongoingrideModel = new OngoingrideModel();
                                            JSONObject ridedata = ridelistArray.getJSONObject(i);
                                            ongoingrideModel.setBooking_id(custo.getString("booking_id"));
                                            ongoingrideModel.setId(ridedata.getString("id"));
                                            ongoingrideModel.setRide_id(ridedata.getString("ride_id"));
                                            ongoingrideModel.setRide_name(ridedata.getString("ride_name"));
                                            if (ridedata.has("ride_desc"))
                                                ongoingrideModel.setRide_desc(ridedata.getString("ride_desc"));
                                            if (ridedata.has("start_time"))
                                                ongoingrideModel.setStatr_time(ridedata.getString("start_time"));
                                            ongoingrideModel.setEnd_time(ridedata.getString("end_time"));
                                            ongoingrideModel.setTotal_ride(ridedata.getString("total_ride"));
                                            ongoingrideModel.setMin_ride_time(ridedata.getString("min_ride_time"));
                                            ongoingrideModel.setRide_img(ridedata.getString("ride_img"));
                                            ongoingrideModel.setColor_code(ridedata.getString("color_code"));
                                            ongoingrideModel.setMin_ride_price(ridedata.getString("min_ride_price"));
                                            ongoingrideModel.setAddition_ride_cost(ridedata.getString("addition_ride_cost"));
                                            ongoingrideModel.setTime_spend(ridedata.getString("time_spend"));
                                            ongoingrideModel.setAddition_ride_time(ridedata.getString("additional_time"));
                                            ongoingrideModel.setStatus(ridedata.getString("status"));
                                            ongoingrideModel.setExtra_per_min_cost(ridedata.getString("extra_per_min_cost"));
                                            ongoingrideModel.setTotal_ride_cost(ridedata.getString("total_ride_cost"));
                                            ongoingrideModel.setRide_time_slot(ridedata.getJSONArray("timeslot").toString());

                                            List<TimeSlotModel> timeslotlist = new ArrayList<>();
                                            try {
                                                JSONArray list = new JSONArray(ongoingrideModel.getRide_time_slot() != null ? ongoingrideModel.getRide_time_slot() : "[]");
                                                for (int k = 0; k < list.length(); k++) {
                                                    JSONObject value = list.getJSONObject(k);
                                                    timeslotlist.add(new TimeSlotModel(value.getString("ride_base_time"), value.getString("ride_base_charge")));
                                                }
                                                ongoingrideModel.setTimeSlot(timeslotlist);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                            ongoingrideModelArrayList.add(ongoingrideModel);

                                        }

                                    }

                                    rideOngoingAdapter.notifyDataSetChanged();
                                    if (ongoingrideModelArrayList.size() > 0) {
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
//                                        btnStopall.setVisibility(View.VISIBLE);
                                    } else {
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);
//                                        btnStopall.setVisibility(View.GONE);

                                    }

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Bookride.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();

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

    private void setupOngoingRecycler() {
        rv_ongoingride.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rideOngoingAdapter = new RideOngoingAdapter(this, ongoingrideModelArrayList);
        rv_ongoingride.setAdapter(rideOngoingAdapter);
    }

    private void setupPendingRecycler() {
        rv_pendingride.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        ridePendingAdapter = new RidePendingAdapter(this, pendingingrideModelArrayList);
        rv_pendingride.setAdapter(ridePendingAdapter);
    }

//    public void cancelride(OngoingrideModel ongoingrideModel) {
//
//        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {
//
//            showProgressDialog();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.cancelBookingUrl,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            Log.i("Response-->", String.valueOf(response));
//
//                            try {
//                                JSONObject result = new JSONObject(String.valueOf(response));
//                                String msg = result.getString("message");
//                                boolean status = result.getBoolean("status");
//
//                                if (status) {
//
//                                    Toast.makeText(Bookride.this, msg, Toast.LENGTH_SHORT).show();
//                                    // selectedList();
//                                    updateAdapter(ongoingrideModel);
//                                    if (ongoingrideModelArrayList.size() > 0) {
//                                        tv_noride.setVisibility(View.GONE);
//                                        btnContinue.setVisibility(View.GONE);
//                                    } else {
//                                        tv_noride.setVisibility(View.VISIBLE);
//                                        btnContinue.setVisibility(View.VISIBLE);
//
//                                    }
//
//                                } else {
//
//                                    Toast.makeText(Bookride.this, "invalid 1", Toast.LENGTH_SHORT).show();
//                                }
//
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//
//                            hideProgressDialog();
//
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            NetworkResponse response = error.networkResponse;
//                            if (error instanceof ServerError && response != null) {
//                                try {
//                                    String res = new String(response.data,
//                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//
//                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();
//
//                                    // Now you can use any deserializer to make sense of data
//                                    JSONObject obj = new JSONObject(res);
//                                } catch (UnsupportedEncodingException e1) {
//                                    // Couldn't properly decode data to string
//                                    e1.printStackTrace();
//                                } catch (JSONException e2) {
//                                    // returned data is not JSONObject?
//                                    e2.printStackTrace();
//                                }
//                            }
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("id", ongoingrideModel.getId());
//
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("Authorization", token);
//                    return params;
//                }
//
//            };
//
//            Volley.newRequestQueue(this).add(stringRequest);
//
//        } else {
//
//            Toast.makeText(getApplicationContext(), "OOPS! No Internet Connection", Toast.LENGTH_SHORT).show();
//
//        }
//
//
//    }

    public void stopride(OngoingrideModel ongoingrideModel, int pos) {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.StopRideUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {

                                    Toast.makeText(Bookride.this, msg, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Bookride.this,Bookride.class);
                                    startActivity(intent);
                                    // selectedList();
//                                    updateAdapter(ongoingrideModel);
                                    updateStopAdapter(ongoingrideModel, pos);
                                    if (ongoingrideModelArrayList.size() > 0) {
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    } else {
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Toast.makeText(Bookride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("ride_id", ongoingrideModel.getId());

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

    private void updateAdapter(OngoingrideModel ongoingrideModel) {
        List<OngoingrideModel> dellist = new ArrayList<>();
        for (int i = 0; i < ongoingrideModelArrayList.size(); i++) {
            if (ongoingrideModel.getBooking_id().equals(ongoingrideModelArrayList.get(i).getBooking_id())) {
                dellist.add(ongoingrideModelArrayList.get(i));
            }
        }
        if (dellist.size() < 3) {
            for (int j = 0; j < dellist.size(); j++) {
                ongoingrideModelArrayList.remove(dellist.get(j));
            }
        } else {
            ongoingrideModelArrayList.remove(ongoingrideModel);
        }
        rideOngoingAdapter.notifyDataSetChanged();
    }


    //Pending

//    public void pendingList() {
//
//
//        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {
//
//
//            showProgressDialog();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.ListbystatusUrl,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            Log.i("Response-->", String.valueOf(response));
//
//                            try {
//                                JSONObject result = new JSONObject(String.valueOf(response));
//                                String msg = result.getString("message");
//                                Log.d(TAG, "msg-->" + msg);
//                                boolean status = result.getBoolean("status");
//                                if (status) {
//                                    pendingingrideModelArrayList.clear();
//                                    JSONObject data = result.getJSONObject("data");
//                                    JSONArray ridelistArray = data.getJSONArray("ridelist");
//                                    for (int i = 0; i < ridelistArray.length(); i++) {
//                                        JSONObject c = ridelistArray.getJSONObject(i);
//                                        JSONObject ridedata = c.getJSONObject("ridedata");
//                                        JSONArray ride_time_slot = c.getJSONArray("ride_time_slot");
//                                        OngoingrideModel ongoingrideModel = new OngoingrideModel();
//
//                                        ongoingrideModel.setRide_time_slot(c.getString("ride_time_slot"));
//
//                                        ongoingrideModel.setId(ridedata.getString("id"));
//                                        ongoingrideModel.setRide_id(ridedata.getString("ride_id"));
//                                        ongoingrideModel.setRide_name(ridedata.getString("ride_name"));
//                                        ongoingrideModel.setRide_desc(ridedata.getString("ride_desc"));
//                                        ongoingrideModel.setRide_img(ridedata.getString("ride_img"));
//                                        ongoingrideModel.setColor_code(ridedata.getString("color_code"));
//                                        ongoingrideModel.setStatus(ridedata.getString("status"));
//                                        ongoingrideModel.setRide_time_slot(ride_time_slot.toString());
//
//                                        pendingingrideModelArrayList.add(ongoingrideModel);
//
////                                        tvrideNumber.setText(ridelistArray.length()+" Ride Available");
//
//                                    }
//
//
//                                    ridePendingAdapter.notifyDataSetChanged();
//                                    if (pendingingrideModelArrayList.size() > 0) {
//                                        tv_noride.setVisibility(View.GONE);
//                                        btnContinue.setVisibility(View.GONE);
////                                        btnStopall.setVisibility(View.VISIBLE);
//                                    } else {
//                                        tv_noride.setVisibility(View.VISIBLE);
//                                        btnContinue.setVisibility(View.VISIBLE);
////                                        btnStopall.setVisibility(View.GONE);
//
//                                    }
//
//                                } else {
//
//                                    Log.d(TAG, "unsuccessfull - " + "Error");
//                                    Toast.makeText(Bookride.this, "invalid", Toast.LENGTH_SHORT).show();
//                                }
//
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            hideProgressDialog();
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            NetworkResponse response = error.networkResponse;
//                            if (error instanceof ServerError && response != null) {
//                                try {
//                                    String res = new String(response.data,
//                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//
//                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();
//
//                                    // Now you can use any deserializer to make sense of data
//                                    JSONObject obj = new JSONObject(res);
//                                } catch (UnsupportedEncodingException e1) {
//                                    // Couldn't properly decode data to string
//                                    e1.printStackTrace();
//                                } catch (JSONException e2) {
//                                    // returned data is not JSONObject?
//                                    e2.printStackTrace();
//                                }
//                            }
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("status", "P");
//
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("Authorization", token);
//                    return params;
//                }
//
//            };
//
//            Volley.newRequestQueue(this).add(stringRequest);
//
//
//        } else {
//
//            Toast.makeText(getApplicationContext(), "OOPS! No Internet Connection", Toast.LENGTH_SHORT).show();
//
//        }
//
//
//    }

    public void startride(OngoingrideModel ongoingrideModel, int pos) {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.StartBookingUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {
                                    ongoingrideModelArrayList.get(pos).setRideOngoning(true);
                                    rideOngoingAdapter.notifyItemChanged(pos);
                                    //rideOngoingAdapter.notifyDataSetChanged();
                                    Toast.makeText(Bookride.this, msg, Toast.LENGTH_SHORT).show();
                                    JSONObject response_data = result.getJSONObject("data");
                                    id = response_data.getString("id");
                                    String ride_id = response_data.getString("ride_id");
                                    String ride_name = response_data.getString("ride_name");
                                    String ride_img = response_data.getString("ride_img");
                                    String color_code = response_data.getString("color_code");
                                    String customer_name = response_data.getString("customer_name");
                                    String customer_mobile = response_data.getString("customer_mobile");
                                    String min_ride_time = response_data.getString("min_ride_time");
                                    String min_ride_price = response_data.getString("min_ride_price");
                                    finish();
                                    startActivity(getIntent());

                                } else {

                                    Toast.makeText(Bookride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("ride_id", ongoingrideModel.getId());
                    params.put("ride_base_time", ridebasetime);
                    params.put("ride_base_charge", ridebasecharge);
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

    public void stopridepending(OngoingrideModel ongoingrideModel, int pos) {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.StopRideUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {
                                    Toast.makeText(Bookride.this, msg, Toast.LENGTH_SHORT).show();
                                    JSONObject response_data = result.getJSONObject("data");
                                    //ongoingrideModelArrayList.remove(ongoingrideModel);

                                    updateStopAdapter(ongoingrideModel, pos);

                                    if (ongoingrideModelArrayList.size() > 0) {
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    } else {
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                    }


                                } else {

                                    Toast.makeText(Bookride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("ride_id", ongoingrideModel.getId());

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

    private void updateStopAdapter(OngoingrideModel ongoingrideModel, int pos) {

        ongoingrideModel.setRideStop(true);
        ongoingrideModel.setRideOngoning(false);

        List<OngoingrideModel> dellist = new ArrayList<>();
        for (int i = 0; i < ongoingrideModelArrayList.size(); i++) {
            if (ongoingrideModel.getBooking_id().equals(ongoingrideModelArrayList.get(i).getBooking_id())) {
                dellist.add(ongoingrideModelArrayList.get(i));
            }
        }
        boolean needtodelAll = false;
        for (int j = 1; j < dellist.size(); j++) {
            if (dellist.get(j).isRideStop()) {
                needtodelAll = true;
            } else {
                needtodelAll = false;
                break;
            }
        }

        if (needtodelAll) {
            for (int j = 0; j < dellist.size(); j++) {
                ongoingrideModelArrayList.remove(dellist.get(j));
            }
            rideOngoingAdapter.notifyDataSetChanged();
        } else {
            rideOngoingAdapter.notifyItemChanged(pos);
        }

        /*if (dellist.size() < 3) {
            for (int j = 0; j < dellist.size(); j++) {
                ongoingrideModelArrayList.remove(dellist.get(j));
            }
        } else {
            ongoingrideModelArrayList.remove(ongoingrideModel);
        }*/

    }


    public void cancelridepending(OngoingrideModel ongoingrideModel) {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.cancelBookingUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {

                                    Toast.makeText(Bookride.this, "Ride Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                    updateAdapter(ongoingrideModel);
                                    Intent intent = new Intent(Bookride.this, Bookride.class);
                                    startActivity(intent);

                                    if (ongoingrideModelArrayList.size() > 0) {
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    } else {
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);
                                    }

                                } else {

                                    Toast.makeText(Bookride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Bookride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("id", ongoingrideModel.getId());

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


    public void alertemergency(OngoingrideModel ongoingrideModel, int pos) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Bookride.this);
        alertDialog.setTitle("Emergency Cancel Ride?");
        View dialogView = LayoutInflater.from(Bookride.this).inflate(R.layout.alert_emergency, null);
        alertDialog.setView(dialogView);
        EditText edt_reason = dialogView.findViewById(R.id.edt_reason);

        alertDialog.setPositiveButton("Yes Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelridepending(ongoingrideModel);
                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();

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

    @Override
    protected void onPause() {
        super.onPause();
        mProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }


    public void onBackPressed() {

        Intent intent = new Intent(Bookride.this, MainActivity.class);
        startActivity(intent);

    }

    public void selectedRideVal(String ridebasetime, String ridebasecharge) {
        Log.v("slected ride", ridebasetime + ridebasecharge);
        this.ridebasetime = ridebasetime;
        this.ridebasecharge = ridebasecharge;
    }


}