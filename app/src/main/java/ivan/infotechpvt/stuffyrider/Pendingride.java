package ivan.infotechpvt.stuffyrider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import ivan.infotechpvt.stuffyrider.adapter.RidePendingAdapter;
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.model.OngoingrideModel;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pendingride extends AppCompatActivity {

    ImageView btn_back;
    String token, id;
    private static final String TAG = "Myapp";
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    private ArrayList<OngoingrideModel> ongoingrideModelArrayList = new ArrayList<>();
    RecyclerView rv_pendingride;
    RidePendingAdapter ridePendingAdapter;
    private String ridebasetime = "";
    private String ridebasecharge = "";
    TextView tv_noride;
    LinearLayout btnContinue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendingride);
        rv_pendingride = (RecyclerView) findViewById(R.id.rv_pendingride);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        tv_noride = findViewById(R.id.tv_noride);
        btnContinue = (LinearLayout) findViewById(R.id.btnContinue);

        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        setupRecycler();
        pendingList();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Pendingride.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Pendingride.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void pendingList() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {


            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.ListbystatusUrl,
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
                                        JSONObject c = ridelistArray.getJSONObject(i);
                                        JSONObject ridedata = c.getJSONObject("ridedata");
                                        JSONArray ride_time_slot = c.getJSONArray("ride_time_slot");
                                        OngoingrideModel ongoingrideModel = new OngoingrideModel();

                                        ongoingrideModel.setRide_time_slot(c.getString("ride_time_slot"));

                                        ongoingrideModel.setId(ridedata.getString("id"));
                                        ongoingrideModel.setRide_id(ridedata.getString("ride_id"));
                                        ongoingrideModel.setRide_name(ridedata.getString("ride_name"));
                                        ongoingrideModel.setRide_desc(ridedata.getString("ride_desc"));
                                        ongoingrideModel.setRide_img(ridedata.getString("ride_img"));
                                        ongoingrideModel.setColor_code(ridedata.getString("color_code"));
                                        ongoingrideModel.setStatus(ridedata.getString("status"));
                                        ongoingrideModel.setRide_time_slot(ride_time_slot.toString());

                                        ongoingrideModelArrayList.add(ongoingrideModel);

//                                        tvrideNumber.setText(ridelistArray.length()+" Ride Available");

                                    }

                                    ridePendingAdapter.notifyDataSetChanged();
                                    if (ongoingrideModelArrayList.size() > 0) {
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    } else {
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Pendingride.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Pendingride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("status", "P");

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

        ridePendingAdapter = new RidePendingAdapter(this, ongoingrideModelArrayList);
        rv_pendingride.setAdapter(ridePendingAdapter);
        rv_pendingride.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


    }


    public void startride(OngoingrideModel ongoingrideModel,int pos) {


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
                                    ridePendingAdapter.notifyDataSetChanged();
                                    Toast.makeText(Pendingride.this, msg, Toast.LENGTH_SHORT).show();
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


                                    // selectedList();
//                                    Intent intent = new Intent(Pendingride.this, Startride.class);
//                                    intent.putExtra("id", id);
//                                    intent.putExtra("ride_id", ride_id);
//                                    intent.putExtra("ride_name", ride_name);
//                                    intent.putExtra("ride_img", ride_img);
//                                    intent.putExtra("color_code", color_code);
//                                    intent.putExtra("cName", customer_name);
//                                    intent.putExtra("phoneNumber", customer_mobile);
//                                    intent.putExtra("ride_base_time", min_ride_time);
//                                    intent.putExtra("ride_base_charge", min_ride_price);
//                                    startActivity(intent);


                                } else {

                                    Toast.makeText(Pendingride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Pendingride.this, res, Toast.LENGTH_SHORT).show();

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


    public void stopride(OngoingrideModel ongoingrideModel,int pos) {


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

                                    Toast.makeText(Pendingride.this, msg, Toast.LENGTH_SHORT).show();
                                    JSONObject response_data = result.getJSONObject("data");
                                   /* ongoingrideModelArrayList.get(pos).setRideOngoning(false);
                                    ridePendingAdapter.notifyDataSetChanged();*/
                                    ongoingrideModelArrayList.remove(pos);
                                    ridePendingAdapter.notifyItemRemoved(pos);
                                    ridePendingAdapter.notifyItemRangeChanged(pos,ongoingrideModelArrayList.size());
//                                    if (ongoingrideModelArrayList.size() == 0) {
//                                        //visible here .your all ride are cancelled
//                                    }



                                    if (ongoingrideModelArrayList.size() > 0) {
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    } else {
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

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

                                    Toast.makeText(Pendingride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Pendingride.this, res, Toast.LENGTH_SHORT).show();

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


    public void cancelride(OngoingrideModel ongoingrideModel) {

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

                                    Toast.makeText(Pendingride.this, msg, Toast.LENGTH_SHORT).show();
                                    // selectedList();
                                    ongoingrideModelArrayList.remove(ongoingrideModel);
                                    ridePendingAdapter.notifyDataSetChanged();
//                                    if (ongoingrideModelArrayList.size() == 0) {
////                                        //visible here .your all ride are cancelled
////                                    }
                                    if (ongoingrideModelArrayList.size() > 0) {
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    } else {
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Toast.makeText(Pendingride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Pendingride.this, res, Toast.LENGTH_SHORT).show();

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

        Intent intent = new Intent(Pendingride.this, MainActivity.class);
        startActivity(intent);

    }


    public void selectedRideVal(String ridebasetime, String ridebasecharge) {
        Log.v("slected ride", ridebasetime + ridebasecharge);
        this.ridebasetime = ridebasetime;
        this.ridebasecharge = ridebasecharge;
    }

}