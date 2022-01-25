package ivan.infotechpvt.stuffyrider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Otpforgotpassword extends AppCompatActivity {

    String user_id;
    private static final String TAG = "Myapp";
    EditText etOTP, etNewpassword, etConfirmpassword;
    Button btnDone;
    String otp, newpassword, confirmpassword, token;
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpforgotpassword);
        etOTP = (EditText)findViewById(R.id.etOTP);
        etNewpassword = (EditText)findViewById(R.id.etNewpassword);
        etConfirmpassword = (EditText)findViewById(R.id.etConfirmpassword);
        btnDone = (Button)findViewById(R.id.btnDone) ;
        btn_back = findViewById(R.id.btn_back);

        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
    }

    public void checkblank(){

        otp = etOTP.getText().toString();
        newpassword = etNewpassword.getText().toString();
        confirmpassword = etConfirmpassword.getText().toString();

        if (otp.length() == 0) {

            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();

        }else if (newpassword.length()==0){

            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();

        } else if (newpassword.length() < 6) {

            Toast.makeText(this, "Please enter atleast six characters", Toast.LENGTH_SHORT).show();

        } else if (confirmpassword.length()==0){

            Toast.makeText(this, "Please Re-enter new password", Toast.LENGTH_SHORT).show();

        }else if (!newpassword.equals(confirmpassword)) {

            Toast.makeText(this, "New password and Re-enter new password is not matched", Toast.LENGTH_SHORT).show();

        } else {

            forgotpassword();
        }


    }


    public void forgotpassword(){

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.ResetPasswordUrl,
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

                                    Toast.makeText(Otpforgotpassword.this, msg, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Otpforgotpassword.this, Login.class);
                                    startActivity(intent);



                                } else {

                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    hideProgressDialog();
                                    Toast.makeText(Otpforgotpassword.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
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

                                    hideProgressDialog();
                                    Toast.makeText(Otpforgotpassword.this, "Invalid OTP", Toast.LENGTH_SHORT).show();

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
                    params.put("user_id", user_id);
                    params.put("otp", otp);
                    params.put("password", confirmpassword);


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