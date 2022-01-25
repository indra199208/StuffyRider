package ivan.infotechpvt.stuffyrider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import ivan.infotechpvt.stuffyrider.adapter.RideselectedAdapter;
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.model.RidelistModel;
import ivan.infotechpvt.stuffyrider.model.TimeSlotModel;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

public class Dashboard extends AppCompatActivity {

    ImageView btn_back;
    String cName, phoneNumber, responsedata, token, id;
    TextView customerName, customerNumber, NoBooking;
    LinearLayout btnStartall;
    private static final String TAG = "Myapp";
    private ArrayList<RidelistModel> ridelistModelArrayList;
    private RideselectedAdapter rideselectedAdapter;
    private RecyclerView rv_selectedrideList;
    private RidelistModel ridelistModel;
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";

    private String ridebasetime = "";
    public String ridebasecharge = "";
    JSONArray bookingrideArray;

    Button btnrideAnother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        customerName = (TextView) findViewById(R.id.customerName);
        customerNumber = (TextView) findViewById(R.id.customerNumber);
        NoBooking = (TextView) findViewById(R.id.NoBooking);
        rv_selectedrideList = (RecyclerView) findViewById(R.id.rv_selectedrideList);
        btnStartall = (LinearLayout) findViewById(R.id.btnStartall);


        btnrideAnother = (Button) findViewById(R.id.btnrideAnother);
        sessionManager = new SessionManager(getApplicationContext());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");


        Intent intent = getIntent();
        cName = intent.getStringExtra("customername");
        phoneNumber = intent.getStringExtra("phonenumber");
        responsedata = intent.getStringExtra("responsedata");

        selectedList();

        if (bookingrideArray.length() > 0) {
            customerName.setText(cName);
            customerName.setVisibility(View.VISIBLE);
            customerNumber.setText("Mobile No. " + phoneNumber);
            btnStartall.setVisibility(View.VISIBLE);
            NoBooking.setVisibility(View.GONE);

        } else {
            customerName.setVisibility(View.GONE);
            customerNumber.setVisibility(View.GONE);
            btnStartall.setVisibility(View.GONE);
            customerName.setVisibility(View.GONE);
            NoBooking.setVisibility(View.VISIBLE);
        }


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        btnrideAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                startActivity(intent);

            }
        });

        btnStartall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray rideDataList = new JSONArray();
                List<Integer> selectedPos = new ArrayList<>();
                for (int r = 0; r < ridelistModelArrayList.size(); r++) {
                    RidelistModel mRidelistModel = ridelistModelArrayList.get(r);
                    if (!mRidelistModel.isRideOngoning()) {
                        for (int t = 0; t < mRidelistModel.getTimeSlot().size(); t++) {
                            TimeSlotModel mTimeSlotModel = mRidelistModel.getTimeSlot().get(t);
                            if (mTimeSlotModel.isSelected()) {
                                selectedPos.add(r);
                                JSONObject rideobj = new JSONObject();
                                try {
                                    rideobj.put("ride_id", mRidelistModel.getId());
                                    rideobj.put("ride_base_time", mTimeSlotModel.getRide_base_time());
                                    rideobj.put("ride_base_charge", mTimeSlotModel.getRide_base_charge());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                rideDataList.put(rideobj);
                            }
                        }
                    }
                }
                if (rideDataList.length() > 0) {
                    Log.v("Start ride date", rideDataList.toString());
                    startAllRide(rideDataList, selectedPos);
                } else {
                    Toast.makeText(Dashboard.this, "Please select ride to start.", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    public void selectedList() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            try {

                JSONObject result = new JSONObject(responsedata);
                String msg = result.getString("message");
                Log.d(TAG, "msg-->" + msg);
                boolean status = result.getBoolean("status");
                if (status) {
                    ridelistModelArrayList = new ArrayList<>();
                    JSONObject data = result.getJSONObject("data");
                    bookingrideArray = data.getJSONArray("data_booking_ride");
                    JSONArray databookingrideArray = data.getJSONArray("data_booking");

                    for (int i = 0; i < bookingrideArray.length(); i++) {
                        JSONObject c = bookingrideArray.getJSONObject(i);
                        ridelistModel = new RidelistModel();
                        ridelistModel.setRide_time_slot(c.getString("ride_time_slot"));

                        JSONObject ride_data = c.getJSONObject("ride_data");
                        ridelistModel.setId(ride_data.getString("id"));
                        ridelistModel.setRide_id(ride_data.getString("ride_id"));
                        ridelistModel.setRide_name(ride_data.getString("ride_name"));
                        ridelistModel.setRide_desc(ride_data.getString("ride_desc"));
                        ridelistModel.setRide_img(ride_data.getString("ride_img"));
                        ridelistModel.setColor_code(ride_data.getString("color_code"));
                        ridelistModel.setStatus(ride_data.getString("status"));


                        List<TimeSlotModel> timeslotlist = new ArrayList<>();
                        try {
                            JSONArray list = new JSONArray(ridelistModel.getRide_time_slot() != null ? ridelistModel.getRide_time_slot() : "[]");
                            for (int j = 0; j < list.length(); j++) {
                                JSONObject value = list.getJSONObject(j);
                                timeslotlist.add(new TimeSlotModel(value.getString("ride_base_time"), value.getString("ride_base_charge")));
                            }
                            ridelistModel.setTimeSlot(timeslotlist);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ridelistModelArrayList.add(ridelistModel);


                    }

                    for (int k = 0; k < databookingrideArray.length(); k++) {
                        JSONObject b = databookingrideArray.getJSONObject(k);
                        ridelistModel.setBooking_id(b.getString("id"));
                        ridelistModel.setCustomer_name(b.getString("customer_name"));
                        ridelistModel.setCustomer_mobile(b.getString("customer_mobile"));


                    }

                    setupRecycler();


                } else {

                    Log.d(TAG, "unsuccessfull - " + "Error");
                    Toast.makeText(Dashboard.this, "invalid", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            hideProgressDialog();


        } else {

            Toast.makeText(getApplicationContext(), "OOPS! No Internet Connection", Toast.LENGTH_SHORT).show();

        }

    }


    private void setupRecycler() {

        rideselectedAdapter = new RideselectedAdapter(this, ridelistModelArrayList);
        rv_selectedrideList.setAdapter(rideselectedAdapter);
        rv_selectedrideList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


    }


    public void cancelride(RidelistModel ridelistModel) {

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

                                    Toast.makeText(Dashboard.this, "Ride Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                    // selectedList();
                                    ridelistModelArrayList.remove(ridelistModel);
                                    rideselectedAdapter.notifyDataSetChanged();
                                    if (ridelistModelArrayList.size() == 0) {
                                        //visible here .your all ride are cancelled
                                    }


                                } else {

                                    Toast.makeText(Dashboard.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Dashboard.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("id", ridelistModel.getId());

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


    public void startride(RidelistModel ridelistModel, int pos) {


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
                                    ridelistModelArrayList.get(pos).setRideOngoning(true);
                                    rideselectedAdapter.notifyItemChanged(pos);
                                    Toast.makeText(Dashboard.this, msg, Toast.LENGTH_SHORT).show();
                                    JSONObject response_data = result.getJSONObject("data");
                                    id = response_data.getString("id");
                                    String ride_id = response_data.getString("ride_id");
                                    String ride_name = response_data.getString("ride_name");
                                    String ride_img = response_data.getString("ride_img");
                                    String color_code = response_data.getString("color_code");
                                    Intent intent = new Intent(Dashboard.this, Bookride.class);
                                    startActivity(intent);


                                    if (ridelistModelArrayList.size() == 0) {
                                        //visible here .your all ride are cancelled
                                    }

                                } else {

                                    Toast.makeText(Dashboard.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Dashboard.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("ride_id", ridelistModel.getId());
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


    public void startAllRide(JSONArray rideDataList, List<Integer> selectedPos) {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.startallride,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                String msg = result.getString("message");
                                boolean status = result.getBoolean("status");

                                if (status) {

                                    for (int i = 0; i < selectedPos.size(); i++) {
                                        ridelistModelArrayList.get(selectedPos.get(i)).setRideOngoning(true);
                                    }
                                    rideselectedAdapter.notifyDataSetChanged();
                                    Intent intent = new Intent(Dashboard.this, Bookride.class);
                                    startActivity(intent);
                                    Toast.makeText(Dashboard.this, msg, Toast.LENGTH_SHORT).show();

                                    if (ridelistModelArrayList.size() == 0) {
                                        //visible here .your all ride are cancelled
                                    }

                                } else {

                                    Toast.makeText(Dashboard.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Dashboard.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("rides", rideDataList.toString());
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


    public void stopride(RidelistModel ridelistModel, int pos) {


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

                                    Toast.makeText(Dashboard.this, msg, Toast.LENGTH_SHORT).show();
                                    JSONObject response_data = result.getJSONObject("data");

//                                    ridelistModelArrayList.remove(ridelistModel);
//                                    rideselectedAdapter.notifyDataSetChanged();

                                    ridelistModelArrayList.remove(pos);
                                    rideselectedAdapter.notifyItemRemoved(pos);
                                    rideselectedAdapter.notifyItemRangeChanged(pos, ridelistModelArrayList.size());


                                    if (ridelistModelArrayList.size() == 0) {
                                        //visible here .your all ride are cancelled
                                    }
//                                    tvTotalAmount.setText("$ " + response_data.getString("total_amount"));
//                                    tvAddprice.setText(response_data.getString("min_ride_price"));
//                                    tvAdditionalmin.setText(response_data.getString("additional_time"));
//                                    tvRate.setText("$ " + ridebasecharge);
//                                    tvRatemin.setText(response_data.getString("min_ride_price"));
                                   /* Date mDatestart = new Date();
                                    mDatestart.setTime(response_data.getLong("start_time"));
                                    Date mDateEnd = new Date();
                                    mDateEnd.setTime(response_data.getLong("end_time"));
                                    long difference = mDateEnd.getTime() - mDatestart.getTime();
                                    int days = (int) (difference / (1000 * 60 * 60 * 24));
                                    int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                                    int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);*/
//                                    tvTotalridetime.setText(time);

                                } else {

                                    Toast.makeText(Dashboard.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Dashboard.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("ride_id", id);

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


    public void onBackPressed() {

        Intent intent = new Intent(Dashboard.this, MainActivity.class);
        startActivity(intent);

    }

    public void selectedRideVal(String ridebasetime, String ridebasecharge) {
        Log.v("slected ride", ridebasetime + ridebasecharge);
        this.ridebasetime = ridebasetime;
        this.ridebasecharge = ridebasecharge;
    }
}