package ivan.infotechpvt.stuffyrider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developers.imagezipper.ImageZipper;
import ivan.infotechpvt.stuffyrider.R;

import ivan.infotechpvt.stuffyrider.retrofit.ApiClient;
import ivan.infotechpvt.stuffyrider.retrofit.ApiInterface;
import ivan.infotechpvt.stuffyrider.session.SessionManager;
import ivan.infotechpvt.stuffyrider.utils.GetRealPathFromUri;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Editprofile extends AppCompatActivity {

    ImageView btnBack, imgPrf;
    TextView tvUsername, tvUserid;
    SessionManager sessionManager;
    private static final String SHARED_PREFS = "sharedPrefs";
    String navuserName, navuserId, token, navuserPic, store_opcode, store_name, store_id;
    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 112;
    private int PICK_IMAGE_REQUEST = 1;
    LinearLayout changePassword;
    EditText etFirstname, etSoreid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        btnBack = (ImageView)findViewById(R.id.btnBack);
        imgPrf = (ImageView)findViewById(R.id.imgPrf);
        tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvUserid = (TextView)findViewById(R.id.tvUserid);
        changePassword = (LinearLayout)findViewById(R.id.changePassword);
        etFirstname = findViewById(R.id.etFirstname);
        etSoreid = findViewById(R.id.etSoreid);

        requestAppPermissions();

        sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        navuserName = sharedPreferences.getString("name", "");
        navuserId = sharedPreferences.getString("id", "");
        token = sharedPreferences.getString("token", "");
        navuserPic = sharedPreferences.getString("image","");
        store_opcode = sharedPreferences.getString("store_opcode", "");
        store_name = sharedPreferences.getString("store_name", "");
        store_id = sharedPreferences.getString("store_id", "");


        Glide.with(Editprofile.this)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/users/"+navuserPic)
                .circleCrop()
                .placeholder(R.drawable.dp)
                .into(imgPrf);

        tvUsername.setText(navuserName);
        tvUserid.setText("ID : "+navuserId);
        etFirstname.setText(store_name);
        etSoreid.setText(store_id);
        etFirstname.setEnabled(false);
        etSoreid.setEnabled(false);


        Glide.with(Editprofile.this)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/users/"+navuserPic)
                .circleCrop()
                .placeholder(R.drawable.dp)
                .into(imgPrf);



        imgPrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showFileChooser();

            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Editprofile.this, Changepassword.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


    }


    private void showFileChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                uploadToServer(GetRealPathFromUri.getPathFromUri(Editprofile.this, filePath));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadToServer(String filePath) throws IOException {
        showProgressDialog();
        Retrofit retrofit = ApiClient.getRetrofitClient(this);
        ApiInterface uploadAPIs = retrofit.create(ApiInterface.class);
        //Create a file object using file path
        File file = new File(filePath);
        File imageZipperFile=new ImageZipper(Editprofile.this)
                .setQuality(30)
                .setMaxWidth(300)
                .setMaxHeight(300)
                .compressToFile(file);

        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/jpg"), imageZipperFile);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("image_file", imageZipperFile.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody userType = RequestBody.create(MediaType.parse("text/plain"), "user");
        //userType

        Call<ResponseBody> mcall = uploadAPIs.uploadImage(part, userType, token);
        mcall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String result = response.body().string();
                        Log.v("response-->", result);
                        JSONObject mjonsresponse = new JSONObject(result);
                        //{"success":true,"STATUSCODE":200,"message":"Profile pic uploaded successfully.","response_data":{"profileImage":"https://nodeserver.brainiuminfotech.com:2100/img/profile-pic/5ee8e2cc87413b5ae1135708-1592569582667.jpg"}}
                        if (mjonsresponse.getBoolean("status")) {
                            hideProgressDialog();
                            String picurl = mjonsresponse.getJSONObject("data").getString("image_name");

                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("image", picurl);
                            editor.apply();

                            Log.v("picurl-->", picurl);
                            Glide.with(Editprofile.this)
                                    .load(picurl)
                                    .circleCrop()
                                    .placeholder(R.drawable.dp)
                                    .into(imgPrf);

                            Intent intent = new Intent(Editprofile.this, Editprofile.class);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v("onFailure", t.getMessage());
            }
        });
    }


    // RUN TIME PERMISSIONS FOR READ AND WRITE

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    // END



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

        Intent intent = new Intent(Editprofile.this, MainActivity.class);
        startActivity(intent);

    }



}