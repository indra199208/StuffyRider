package ivan.infotechpvt.stuffyrider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import ivan.infotechpvt.stuffyrider.R;

import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Startride extends AppCompatActivity {

    LinearLayout btnEnd, btnafterEnd;
    LinearLayout ll_endRide, ll_bg;
    ImageView btn_back, riderImage, btnEmergency;
    String name, mobile, picture, ridename, colorcode, ridebasetime, ridebasecharge, ride_id, token, id;
    TextView tvUsername, tvMobile, tvRidername, tv_timeandrate, tvTimer, tvTotalAmount, tvAddprice, tvAdditionalmin, tvRate, tvRatemin, tvTotalridetime, tvTimerecent, tvStoptime;
    private int seconds = 0;
    private boolean running;
    private boolean wasRunning;
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    Button btnrideAnother;

    String time;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startride);
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        startTimer();
        runTimer();
        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        btnEnd = (LinearLayout) findViewById(R.id.btnEnd);
        btnafterEnd = (LinearLayout) findViewById(R.id.btnafterEnd);
        ll_endRide = (LinearLayout) findViewById(R.id.ll_endRide);
        ll_bg = (LinearLayout) findViewById(R.id.ll_bg);
        riderImage = (ImageView) findViewById(R.id.riderImage);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvMobile = (TextView) findViewById(R.id.tvMobile);
        tvRidername = (TextView) findViewById(R.id.tvRidername);
        tv_timeandrate = (TextView) findViewById(R.id.tv_timeandrate);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvTimerecent = (TextView) findViewById(R.id.tvTimerecent);
        tvStoptime = (TextView) findViewById(R.id.tvStoptime);
        btnEmergency = (ImageView) findViewById(R.id.btnEmergency);
        btnrideAnother = (Button)findViewById(R.id.btnrideAnother);

        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        tvAddprice = (TextView) findViewById(R.id.tvAddprice);
        tvAdditionalmin = (TextView) findViewById(R.id.tvAdditionalmin);
        tvRate = (TextView) findViewById(R.id.tvRate);
        tvRatemin = (TextView) findViewById(R.id.tvRatemin);
        tvTotalridetime = (TextView) findViewById(R.id.tvTotalridetime);


        Intent intent = getIntent();
        name = intent.getStringExtra("cName");
        mobile = intent.getStringExtra("phoneNumber");
        picture = intent.getStringExtra("ride_img");
        ridename = intent.getStringExtra("ride_name");
        colorcode = intent.getStringExtra("color_code");
        ridebasetime = intent.getStringExtra("ride_base_time");
        ridebasecharge = intent.getStringExtra("ride_base_charge");
        ride_id = intent.getStringExtra("ride_id");
        id = intent.getStringExtra("id");

        LocalTime localTime = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");


        tvUsername.setText(name);
        tvMobile.setText(mobile);
        tvRidername.setText(ridename);
        tv_timeandrate.setText("$" + ridebasecharge + "/" + ridebasetime + " min");
        tvTimerecent.setText("" + localTime.format(dateTimeFormatter));

        Glide.with(this)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/ride/" + picture)
                .placeholder(R.drawable.image2)
                .into(riderImage);

        if (colorcode.equals("#FB7FA5")) {

            ll_bg.setBackgroundResource(R.drawable.border_shape8);

        } else if (colorcode.equals("#FFC423")) {

            ll_bg.setBackgroundResource(R.drawable.border_shape14);

        } else if (colorcode.equals("#1122C1")) {

            ll_bg.setBackgroundResource(R.drawable.border_shape15);

        } else if (colorcode.equals("#009688")) {

            ll_bg.setBackgroundResource(R.drawable.border_shape16);
        } else {

            ll_bg.setBackgroundResource(R.drawable.border_shape8);
        }


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Startride.this, Pendingride.class);
                startActivity(intent);

            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopride();
                stopTimer();
                ll_endRide.setVisibility(View.VISIBLE);
                btnEnd.setVisibility(View.GONE);
                btnafterEnd.setVisibility(View.VISIBLE);
                tvStoptime.setText("" + localTime.format(dateTimeFormatter));
                tvStoptime.setVisibility(View.VISIBLE);


            }
        });

        btnrideAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Startride.this, MainActivity.class);
                startActivity(intent);

            }
        });

        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelride();
            }
        });

    }


    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState
                .putBoolean("running", running);
        savedInstanceState
                .putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }


    public void startTimer() {
        running = true;
    }

    public void stopTimer() {
        running = false;
    }


    private void runTimer() {


        final Handler handler
                = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;


                time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);

                tvTimer.setText(time);

                if (running) {
                    seconds++;
                }


                handler.postDelayed(this, 1000);
            }
        });
    }

    public void cancelride() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);//Here I have to use v.getContext() istead of just cont.
        alertDialog.setTitle("Cancel Ride?");
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        cancelrideapi();

                    }
                });

        alertDialog.show();

    }


    public void cancelrideapi(){


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

                                    Toast.makeText(Startride.this, msg, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Startride.this, MainActivity.class);
                                    startActivity(intent);


                                } else {

                                    Toast.makeText(Startride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Startride.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("id", id);

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


    public void stopride() {


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

                                    Toast.makeText(Startride.this, msg, Toast.LENGTH_SHORT).show();
                                    JSONObject response_data = result.getJSONObject("data");

                                    tvTotalAmount.setText("$ " + response_data.getString("total_amount"));
                                    tvAddprice.setText(response_data.getString("min_ride_price"));
                                    tvAdditionalmin.setText(response_data.getString("additional_time"));
                                    tvRate.setText("$ " + ridebasecharge);
                                    tvRatemin.setText(response_data.getString("min_ride_price"));
                                   /* Date mDatestart = new Date();
                                    mDatestart.setTime(response_data.getLong("start_time"));
                                    Date mDateEnd = new Date();
                                    mDateEnd.setTime(response_data.getLong("end_time"));
                                    long difference = mDateEnd.getTime() - mDatestart.getTime();
                                    int days = (int) (difference / (1000 * 60 * 60 * 24));
                                    int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                                    int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);*/
                                    tvTotalridetime.setText(time);

                                } else {

                                    Toast.makeText(Startride.this, "invalid 1", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(Startride.this, res, Toast.LENGTH_SHORT).show();

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

        Intent intent = new Intent(Startride.this, Pendingride.class);
        startActivity(intent);

    }


}