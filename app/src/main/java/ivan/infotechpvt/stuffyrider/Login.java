package ivan.infotechpvt.stuffyrider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.allurl.AllUrl;
import ivan.infotechpvt.stuffyrider.internet.CheckConnectivity;
import ivan.infotechpvt.stuffyrider.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btnSignup;
    EditText etStoreid, etPassword, etOpcode;
    private static final String TAG = "Myapp";
    private static final String SHARED_PREFS = "sharedPrefs";
    Context context;
    String storeid, password, opcode, msg;
    SessionManager sessionManager;
    CheckBox btnCheck;
    TextView btnForgotpassword;

    String id, store_id, role, opCode, name, email, email_verified, phone, phone_verified, sms_permission, status_, image, store_name, store_opcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        etStoreid = (EditText) findViewById(R.id.etStoreid);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etOpcode = (EditText) findViewById(R.id.etOpcode);
        btnCheck = (CheckBox) findViewById(R.id.btnCheck);
        btnForgotpassword = (TextView) findViewById(R.id.btnForgotpassword);

        sessionManager = new SessionManager(getApplicationContext());


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkblank();

            }
        });


        btnForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, Forgotpassoword.class);
                startActivity(intent);
            }
        });


    }

    public void checkblank() {

        storeid = etStoreid.getText().toString();
        password = etPassword.getText().toString();
        opcode = etOpcode.getText().toString();

        if (storeid.length() == 0) {

            Toast.makeText(this, "Please enter a Valid Email Id", Toast.LENGTH_SHORT).show();

        } else if (opcode.length() == 0) {


            Toast.makeText(this, "Please enter a Opcode", Toast.LENGTH_SHORT).show();


        } else if (password.length() == 0) {

            Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();

        } else {
            login();
        }


    }


    public void login() {

        if (CheckConnectivity.getInstance(getApplicationContext()).isOnline()) {

            showProgressDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AllUrl.loginUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("Response-->", String.valueOf(response));

                            try {
                                JSONObject result = new JSONObject(String.valueOf(response));
                                msg = result.getString("message");
                                Log.d(TAG, "msg-->" + msg);
                                boolean status = result.getBoolean("status");
                                if (status) {

                                    Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();

                                    JSONArray ridelistArray = result.getJSONArray("data");
                                    JSONArray storeArray = result.getJSONArray("store_data");

                                    for (int i = 0; i < ridelistArray.length(); i++) {
                                        JSONObject jb = (JSONObject) ridelistArray.get(i);
                                        id = jb.getString("id");
                                        role = jb.getString("role");
                                        name = jb.getString("name");
                                        email = jb.getString("email");
                                        email_verified = jb.getString("email_verified");
                                        phone = jb.getString("phone");
                                        phone_verified = jb.getString("phone_verified");
                                        sms_permission = jb.getString("sms_permission");
                                        status_ = jb.getString("status");
                                        image = jb.getString("image");

                                    }

                                    for (int j = 0; j < storeArray.length(); j++) {
                                        JSONObject storeobj = storeArray.getJSONObject(j);
                                        store_name = storeobj.getString("store_name");
                                        store_opcode = storeobj.getString("opcode");
                                        store_id = storeobj.getString("id");

                                    }

                                    String token = result.getString("token");

                                    //SharedPref
                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("role", role);
                                    editor.putString("opcode", opcode);
                                    editor.putString("name", name);
                                    editor.putString("email", email);
                                    editor.putString("id", id);
                                    editor.putString("email_verified", email_verified);
                                    editor.putString("phone_verified", phone_verified);
                                    editor.putString("sms_permission", sms_permission);
                                    editor.putString("status_p", status_);
                                    editor.putString("image", image);
                                    editor.putString("token", token);
                                    editor.putString("phone", phone);
                                    editor.putString("store_name", store_name);
                                    editor.putString("store_id", store_id);

                                    editor.apply();

                                    if (btnCheck.isChecked()) {

                                        sessionManager.createLoginSession(storeid, password);
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {

                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }


                                } else {

                                    hideProgressDialog();
                                    Log.d(TAG, "unsuccessfull - " + "Error");
                                    Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
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
                            hideProgressDialog();
                            Toast.makeText(Login.this, "Wrong email address, opcode or password entered", Toast.LENGTH_SHORT).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", storeid);
                    params.put("password", password);
                    params.put("opcode", opcode);

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


    @Override
    public void onBackPressed() {

        finishAffinity();
    }


}