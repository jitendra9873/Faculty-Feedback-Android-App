package api.feedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    // Login Preferences
    public static final String SP_LOGIN_ID = "LoginPreferences";
    public static final String SP_LOGIN_LOGGED_IN_STATE = "LoggedInState";
    public static final String SP_LOGIN_USER_SAP = "UserSAP";
    public static final String SP_LOGIN_CLASS = "UserClass";

    // Lock to avoid deadlock
    private boolean lockLogin = false;

    // UI references.
    private EditText sapET;
    private EditText passwordView;
    private ProgressDialog progressDialog;

    //String login pass
    String[][] loginData = {
            {"60004150015", "60004150015", "SE-A"},
            {"60004150035", "60004150015", "SE-B"},
            {"60004150011", "60004150015", "SE-A"},
            {"60004140002", "60004140002", "TE-A"},
            {"60004130102", "60004130102", "BE-B"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        sapET = (EditText) findViewById(R.id.login_username);

        passwordView = (EditText) findViewById(R.id.login_password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button emailSignInButton = (Button) findViewById(R.id.login_sign_in);
        emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        setupProgressDialog();
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
    }

    private void attemptLogin() {
        if (lockLogin) {
            return;
        }

        // Reset errors.
        sapET.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = sapET.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isValidPassword(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            sapET.setError(getString(R.string.error_field_required));
            focusView = sapET;
            cancel = true;
        } else if (!isValidSAP(email)) {
            sapET.setError(getString(R.string.error_invalid_email));
            focusView = sapET;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.setMessage(getString(R.string.login_authenticating));
            progressDialog.show();
            signIn(email, password);
        }
    }

    static boolean isValidSAP(String sap) {
        return sap.matches("[-+]?\\d*\\.?\\d+");
    }

    static boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    void signIn(final String sap, final String password) {

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPreExecute(){
                lockLogin = true;
            }

            @Override
            protected Integer doInBackground(Void... params) {
                int idx = -1;
                for(int i=0; i<loginData.length; i++){
                    if(Objects.equals(loginData[i][0], sap)){
                        if(Objects.equals(loginData[i][1], password)){
                            idx = i;
                            break;
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return idx;
            }

            @Override
            protected void onPostExecute(Integer idx){
                if(idx != -1){
                    SharedPreferences prefs = getSharedPreferences(SP_LOGIN_ID, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putBoolean(SP_LOGIN_LOGGED_IN_STATE, true);
                    editor.putString(SP_LOGIN_USER_SAP, sap);
                    editor.putString(SP_LOGIN_CLASS, loginData[idx][2]);

                    editor.apply();

                    finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else{
                    sapET.setError(getString(R.string.error_incorrect_password));
                    passwordView.setError(getString(R.string.error_incorrect_password));
                    passwordView.requestFocus();
                }
                lockLogin = false;
                progressDialog.dismiss();
            }
        }.execute();
    }
}
