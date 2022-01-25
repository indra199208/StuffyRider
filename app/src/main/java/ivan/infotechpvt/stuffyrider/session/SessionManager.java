package ivan.infotechpvt.stuffyrider.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import ivan.infotechpvt.stuffyrider.Login;

public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "NWS_Pref";

    private static final String IS_LOGIN = "IsLoggedIn";


    //username
    public static final String KEY_username = "username";

    //password
    public static final String KEY_password = "password";

    public static final String KEY_batchcount = "batchcount";
    public static final String KEY_VENDOR = "key_vandor";
    public static final String KEY_ADDRESS = "key_address";





    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Create login session
     */
    public void createLoginSession(String username, String password) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_username, username);
        editor.putString(KEY_password, password);
        editor.commit();
    }


    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Long.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);
        }
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    // Get Login State
    public boolean isLoggedIn() {

        return pref.getBoolean(IS_LOGIN, false);
    }





}
