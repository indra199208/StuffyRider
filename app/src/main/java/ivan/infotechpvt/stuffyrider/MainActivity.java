package ivan.infotechpvt.stuffyrider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import ivan.infotechpvt.stuffyrider.adapter.RidelistAdapter;
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.model.RidelistModel;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Myapp";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    LinearLayout btnRent;
    LinearLayout btnContinue, btnRideOngoing, btnRidepending, btnBookride;
    private BottomSheetBehavior mBottomSheetBehavior1, mBottomSheetBehavior2;
    EditText etcustomerName, etPhone;
    LinearLayout btnNext, btnSkip;
    RidelistModel mRidelistModel;
    ImageView navprofilePic, imgSearch;
    LinearLayout btnRidehistory, btnLogout;
    String navuserName, navuserId, token, navuserPic, storename, customername, phonenumber;
    TextView userName, userId, tvrideNumber, tvRentout, tvStorename;
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    private ArrayList<RidelistModel> ridelistModelArrayList = new ArrayList<>();

    private RidelistAdapter ridelistAdapter;
    private RecyclerView rv_rideList;
    int totalyescount = 0;
    int totalblock = 0;
    LinearLayout btnReady, btnRideAvailable, btnPendingrides, btnOngoingride, btnRidehis;
    SwipeRefreshLayout pullToRefresh;
    JSONArray selectedarray;
    String lastChar = " ";
    String cname, phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnRidehistory = (LinearLayout) findViewById(R.id.btnRidehistory);
        btnLogout = (LinearLayout) findViewById(R.id.btnLogout);
        btnRideAvailable = findViewById(R.id.btnRideAvailable);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);
//        btnSkip = (LinearLayout) findViewById(R.id.btnSkip);
        btnBookride = findViewById(R.id.btnBookride);
        btnRidehis = findViewById(R.id.btnRidehis);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        tvrideNumber = (TextView) findViewById(R.id.tvrideNumber);
        tvRentout = (TextView) findViewById(R.id.tvRentout);
        tvStorename = (TextView) findViewById(R.id.tvStorename);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        View bottomSheet2 = findViewById(R.id.customer_sheet);
        etcustomerName = (EditText) findViewById(R.id.etcustomerName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);

        sessionManager = new SessionManager(getApplicationContext());


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        navuserName = sharedPreferences.getString("name", "");
        navuserId = sharedPreferences.getString("id", "");
        token = sharedPreferences.getString("token", "");
        navuserPic = sharedPreferences.getString("image", "");
        storename = sharedPreferences.getString("store_name", "");

//        Intent intent = getIntent();
//        customername = intent.getStringExtra("customername");
//        phonenumber = intent.getStringExtra("phonenumber");

//        if (customername == null) {
//            customername = "";
//        } else {
//            customername = intent.getStringExtra("customername");
//        }
//
//        if (phonenumber == null) {
//            phonenumber = "";
//        } else {
//            phonenumber = intent.getStringExtra("phonenumber");
//        }

        btnRent = findViewById(R.id.btnRent);
        btnContinue = (LinearLayout) findViewById(R.id.btnContinue);
        toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorHighlight));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.navuserName);
        userId = header.findViewById(R.id.navuserId);
        rv_rideList = (RecyclerView) findViewById(R.id.rv_rideList);
        navprofilePic = header.findViewById(R.id.ImageProfile);

        userName.setText(navuserName);
        userId.setText("ID: " + navuserId);
        tvStorename.setText(storename);


        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int digits = etPhone.getText().toString().length();
                if (digits > 1)
                    lastChar = etPhone.getText().toString().substring(digits - 1);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int digits = etPhone.getText().toString().length();
                Log.d("LENGTH", "" + digits);
                if (!lastChar.equals("-")) {
                    if (digits == 3 || digits == 7) {
                        etPhone.append("-");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });

//        btnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//            }
//        });


        Glide.with(MainActivity.this)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/users/" + navuserPic)
                .circleCrop()
                .placeholder(R.drawable.dp)
                .into(navprofilePic);


        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.iv_menu).setOnClickListener(view -> {
            setDrawerLocked();
        });


        navprofilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Editprofile.class);
                startActivity(intent);

            }
        });


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etcustomerName.getText().toString().length() == 0 || etPhone.getText().toString().length() == 0) {

                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

                } else {

                    selectedarray = new JSONArray();
                    assert ridelistModelArrayList != null;
                    if (ridelistModelArrayList.size() > 0) {
                        for (RidelistModel mRidelistModel : ridelistModelArrayList) {
                            if (mRidelistModel.isSelected()) {
                                JSONObject mJsonObject = new JSONObject();
                                try {
                                    mJsonObject.put("ride_id", mRidelistModel.getId());
                                    selectedarray.put(mJsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (selectedarray.length() > 0) {
                            bookingride();
                        }else{
                            Toast.makeText(MainActivity.this,"Please select atleast one ride to continue.",Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }


        });

        btnRidehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Ridehistory.class);
                startActivity(intent);

            }
        });


        btnBookride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Bookride.class);
                startActivity(intent);

            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // builder.setCancelable(false);
                builder.setMessage("Do you really want to logout?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sessionManager.logoutUser();
                        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        dialog.cancel();

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        btnRideAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        btnRidehis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this, Ridehistory.class);
                startActivity(intent);
            }
        });

        setupRecycler();

        rideList();


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                rideList();

//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });

        rv_rideList.setVisibility(View.GONE);


    }


    public void checkblank() {

        cname = etcustomerName.getText().toString();
        phone = etPhone.getText().toString();
        Pattern pattern = Pattern.compile("[$&+,:;=\\\\\\\\?@#|/'<>.^N*()%! ]");


        if (cname.length() == 0) {

            Toast.makeText(this, "Please enter a Customer Name", Toast.LENGTH_SHORT).show();

        } else if (phone.length() == 0 || phone.length() < 12 || pattern.matcher(phone).find()) {

            Toast.makeText(this, "Please enter a Valid Phone number", Toast.LENGTH_SHORT).show();

        } else {

            this.mBottomSheetBehavior2.setHideable(true);
            mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            rv_rideList.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }


    }


    public void bookingride() {


        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.bookingrideUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            // [{"ride_id":1,"ride_time":10,"ride_price":8},{"ride_id":2,"ride_time":15,"ride_price":25},{"ride_id":3,"ride_time":20,"ride_price":30}]//
                            //[{"ride_id":1},{"ride_id":1},{"ride_id":1}]

                            hideProgressDialog();


                            Intent intent = new Intent(MainActivity.this, Dashboard.class);
                            intent.putExtra("customername", cname);
                            intent.putExtra("phonenumber", phone);
                            intent.putExtra("responsedata", String.valueOf(response));
                            startActivity(intent);


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

                                    Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();

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
                    params.put("name", cname);
                    params.put("mobile", phone);
                    params.put("ride", selectedarray.toString());

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


    public void rideList() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefresh.setRefreshing(false);
            }
        }, 1000);

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, AllUrl.rideListUrl,
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
                                    ridelistModelArrayList.clear();
                                    totalyescount = 0;
                                    totalblock = 0;
                                    ridelistAdapter.notifyDataSetChanged();
                                    JSONObject data = result.getJSONObject("data");
                                    JSONArray ridelistArray = data.getJSONArray("ridelist");

                                    for (int i = 0; i < ridelistArray.length(); i++) {

                                        RidelistModel ridelistModel = new RidelistModel();
                                        JSONObject ridelistobj = ridelistArray.getJSONObject(i);
                                        ridelistModel.setId(ridelistobj.getString("id"));
                                        ridelistModel.setRide_name(ridelistobj.getString("ride_name"));
                                        ridelistModel.setRide_desc(ridelistobj.getString("ride_desc"));
                                        ridelistModel.setRide_img(ridelistobj.getString("ride_img"));
                                        ridelistModel.setColor_code(ridelistobj.getString("color_code"));
                                        ridelistModel.setAvailibility(ridelistobj.getString("availibility"));
                                        ridelistModelArrayList.add(ridelistModel);

                                        if (ridelistobj.getString("availibility").equalsIgnoreCase("yes")) {
                                            totalyescount++;
                                        }

                                        if (ridelistobj.getString("availibility").equalsIgnoreCase("no")) {
                                            totalblock++;
                                        }


                                    }

                                    tvrideNumber.setText(totalyescount + " Rides Available");
                                    tvRentout.setText(totalblock + " Rent Out");


                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(MainActivity.this, "invalid", Toast.LENGTH_SHORT).show();
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

                                    Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();

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


    private void setupRecycler() {

        ridelistAdapter = new RidelistAdapter(this, ridelistModelArrayList);
        rv_rideList.setAdapter(ridelistAdapter);
        rv_rideList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }


    public void setDrawerLocked() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }


    public void bottomsheetopen(RidelistModel ridelistModel) {
        boolean isOpen = false;
        for (int i = 0; i < ridelistModelArrayList.size(); i++) {

            if (ridelistModelArrayList.get(i).isSelected()) {
                isOpen = true;
                break;
            } else {
                isOpen = false;
            }
        }

        if (isOpen) {

            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    public void bottomsheetopendirect(RidelistModel ridelistModel) {

        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    tvStatus.setText("Close");
    }

    public void bottomsheetclose(RidelistModel ridelistModel) {

        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    tvStatus.setText("Close");
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
// TODO Auto-generated method stub
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // builder.setCancelable(false);
            builder.setMessage("Do you want to Exit?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    finishAffinity();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                    dialog.cancel();

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}