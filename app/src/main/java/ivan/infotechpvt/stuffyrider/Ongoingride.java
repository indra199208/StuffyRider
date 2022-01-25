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
import ivan.infotechpvt.stuffyrider.adapter.RideOngoingAdapter;
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

import static ivan.infotechpvt.stuffyrider.Ongoingdetails.needToRefresh;

public class Ongoingride extends AppCompatActivity {

    ImageView btn_back;
    String token;
    private static final String TAG = "Myapp";
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    private OngoingrideModel ongoingrideModel;
    private ArrayList<OngoingrideModel> ongoingrideModelArrayList = new ArrayList<>();
    RideOngoingAdapter rideOngoingAdapter;
    RecyclerView rv_ongoingride;
    TextView tv_noride;
    LinearLayout btnContinue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoingride);
        rv_ongoingride = (RecyclerView) findViewById(R.id.rv_ongoingride);
        tv_noride =  findViewById(R.id.tv_noride);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btnContinue = (LinearLayout)findViewById(R.id.btnContinue);
        sessionManager = new SessionManager(getApplicationContext());

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        setupRecycler();
        ongoingList();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Ongoingride.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Ongoingride.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(needToRefresh){
            needToRefresh  = false;
            ongoingList();
        }
    }

    public void ongoingList() {


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
                                    ongoingrideModelArrayList.clear();
                                    JSONObject data = result.getJSONObject("data");
                                    JSONArray ridelistArray = data.getJSONArray("ridelist");
                                    for (int i = 0; i < ridelistArray.length(); i++) {

                                        OngoingrideModel ongoingrideModel = new OngoingrideModel();
                                        JSONObject ridelistobj = ridelistArray.getJSONObject(i);
                                        JSONObject ridedata = ridelistobj.getJSONObject("ridedata");

                                        ongoingrideModel.setId(ridedata.getString("id"));
                                        ongoingrideModel.setRide_id(ridedata.getString("ride_id"));
                                        ongoingrideModel.setRide_name(ridedata.getString("ride_name"));
                                        ongoingrideModel.setRide_desc(ridedata.getString("ride_desc"));
                                        ongoingrideModel.setStatr_time(ridedata.getString("statr_time"));
                                        ongoingrideModel.setRide_img(ridedata.getString("ride_img"));
                                        ongoingrideModel.setColor_code(ridedata.getString("color_code"));
                                        ongoingrideModel.setMin_ride_price(ridedata.getString("min_ride_price"));
                                        ongoingrideModel.setStatus(ridedata.getString("status"));


                                        ongoingrideModelArrayList.add(ongoingrideModel);

//                                        tvrideNumber.setText(ridelistArray.length()+" Ride Available");

                                    }

                                    rideOngoingAdapter.notifyDataSetChanged();
                                    if(ongoingrideModelArrayList.size()>0){
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    }else{
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Ongoingride.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ongoingride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("status", "A");

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

        rideOngoingAdapter = new RideOngoingAdapter(this, ongoingrideModelArrayList);
        rv_ongoingride.setAdapter(rideOngoingAdapter);
        rv_ongoingride.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


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

                                    Toast.makeText(Ongoingride.this, msg, Toast.LENGTH_SHORT).show();
                                    // selectedList();
                                    ongoingrideModelArrayList.remove(ongoingrideModel);
                                    rideOngoingAdapter.notifyDataSetChanged();
                                    if(ongoingrideModelArrayList.size()>0){
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    }else{
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Toast.makeText(Ongoingride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ongoingride.this, res, Toast.LENGTH_SHORT).show();

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


    public void stopride(OngoingrideModel ongoingrideModel) {


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

                                    Toast.makeText(Ongoingride.this, msg, Toast.LENGTH_SHORT).show();
                                    // selectedList();
                                    ongoingrideModelArrayList.remove(ongoingrideModel);
                                    rideOngoingAdapter.notifyDataSetChanged();
                                    if(ongoingrideModelArrayList.size()>0){
                                        tv_noride.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.GONE);
                                    }else{
                                        tv_noride.setVisibility(View.VISIBLE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                    }

                                } else {

                                    Toast.makeText(Ongoingride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Ongoingride.this, res, Toast.LENGTH_SHORT).show();

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

        Intent intent = new Intent(Ongoingride.this, MainActivity.class);
        startActivity(intent);

    }
}