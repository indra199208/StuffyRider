package ivan.infotechpvt.stuffyrider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import ivan.infotechpvt.stuffyrider.R;

import ivan.infotechpvt.stuffyrider.session.SessionManager;

public class Splash extends AppCompatActivity {


    private static final String TAG = "myapp";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(getApplicationContext());



        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (sessionManager.isLoggedIn()){

                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {

                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, secondsDelayed * 3000);
    }
}