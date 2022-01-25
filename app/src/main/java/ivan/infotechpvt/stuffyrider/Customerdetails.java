package ivan.infotechpvt.stuffyrider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.session.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.regex.Pattern;

public class Customerdetails extends AppCompatActivity {

    private static final String TAG = "Myapp";
    private BottomSheetBehavior mBottomSheetBehavior1;
    LinearLayout btnNext;
    EditText etcustomerName, etPhone;
    String customername, phone, colorcode;
    SessionManager sessionManager;
    String navuserName, navuserId, token;
    String lastChar = " ";

    private static final String SHARED_PREFS = "sharedPrefs";
    private String selectedRide="[]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerdetails);
//        selectedRide = Objects.requireNonNull(getIntent().getExtras()).getString("selectedRide");
        View bottomSheet = findViewById(R.id.customer_sheet);
        etcustomerName = (EditText) findViewById(R.id.etcustomerName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        this.mBottomSheetBehavior1.setHideable(false);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

        sessionManager = new SessionManager(getApplicationContext());


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        navuserName = sharedPreferences.getString("name", "");
        navuserId = sharedPreferences.getString("id", "");
        token = sharedPreferences.getString("token", "");

        btnNext = (LinearLayout) findViewById(R.id.btnNext);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int digits = etPhone.getText().toString().length();
                if (digits > 1)
                    lastChar = etPhone.getText().toString().substring(digits-1);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int digits = etPhone.getText().toString().length();
                Log.d("LENGTH",""+digits);
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

    }


    public void checkblank() {

        customername = etcustomerName.getText().toString();
        phone = etPhone.getText().toString();
        Pattern pattern = Pattern.compile("[$&+,:;=\\\\\\\\?@#|/'<>.^N*()%! ]");


        if (customername.length() == 0) {

            Toast.makeText(this, "Please enter a Customer Name", Toast.LENGTH_SHORT).show();

        } else if (phone.length() == 0 || phone.length()<12 || pattern.matcher(phone).find()) {

            Toast.makeText(this, "Please enter a Valid Phone number", Toast.LENGTH_SHORT).show();

        } else {

            Intent intent = new Intent(Customerdetails.this, MainActivity.class);
            intent.putExtra("customername", customername);
            intent.putExtra("phonenumber", phone);
            startActivity(intent);


//            bookingride();

        }


    }

//    public void bookingride(){
//
//
//        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {
//
//            showProgressDialog();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.bookingrideUrl,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            Log.i("Response-->", String.valueOf(response));
//
//                           // [{"ride_id":1,"ride_time":10,"ride_price":8},{"ride_id":2,"ride_time":15,"ride_price":25},{"ride_id":3,"ride_time":20,"ride_price":30}]//
//                            //[{"ride_id":1},{"ride_id":1},{"ride_id":1}]
//
//                            hideProgressDialog();
//
//
//                            Intent intent = new Intent(Customerdetails.this, Dashboard.class);
//                            intent.putExtra("customername", customername);
//                            intent.putExtra("phonenumber", phone);
//                            intent.putExtra("responsedata", String.valueOf(response));
//                            startActivity(intent);
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
//                                    Toast.makeText(Customerdetails.this, res, Toast.LENGTH_SHORT).show();
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
//                    params.put("name", customername);
//                    params.put("mobile", phone);
//                    params.put("ride", selectedRide);
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
//
//    }

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