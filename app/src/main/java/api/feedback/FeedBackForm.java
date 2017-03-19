package api.feedback;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.appyvet.rangebar.RangeBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FeedBackForm extends AppCompatActivity {

    public static final int FORM_TYPE_THEORY = 1;
    public static final int FORM_TYPE_PRACS = 2;

    RangeBar r1, r2, r3, r4, r5, r6, r7, r8, r9;
    EditText commentET;

    int formType = FORM_TYPE_THEORY;
    String teacherName = "";
    String subject = "";
    String division = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        teacherName = intent.getStringExtra("teacher");
        subject = intent.getStringExtra("subject");
        division = intent.getStringExtra("division");
        if(intent.getBooleanExtra("isPracs", false))
            formType = FORM_TYPE_PRACS;
        else
            formType = FORM_TYPE_THEORY;

        if(formType == FORM_TYPE_THEORY){
            setTitle("Theory Feedback");
            setContentView(R.layout.activity_theory_feedback);

            r6 = (RangeBar) findViewById(R.id.rangebar_6);
            r7 = (RangeBar) findViewById(R.id.rangebar_7);
            r8 = (RangeBar) findViewById(R.id.rangebar_8);
            r9 = (RangeBar) findViewById(R.id.rangebar_9);

            r6.setSeekPinByIndex(2);
            r7.setSeekPinByIndex(2);
            r8.setSeekPinByIndex(2);
            r9.setSeekPinByIndex(2);
        }
        else{
            setTitle("Practical Feedback");
            setContentView(R.layout.activity_pracs_feedback);
        }

        r1 = (RangeBar) findViewById(R.id.rangebar_1);
        r2 = (RangeBar) findViewById(R.id.rangebar_2);
        r3 = (RangeBar) findViewById(R.id.rangebar_3);
        r4 = (RangeBar) findViewById(R.id.rangebar_4);
        r5 = (RangeBar) findViewById(R.id.rangebar_5);

        r1.setSeekPinByIndex(2);
        r2.setSeekPinByIndex(2);
        r3.setSeekPinByIndex(2);
        r4.setSeekPinByIndex(2);
        r5.setSeekPinByIndex(2);

        commentET = (EditText) findViewById(R.id.comment_th);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FeedBackForm.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                builder.setTitle("Send Feedback")
                        .setMessage("Once feedback is sent, you cannot edit it. Are you sure with your feedback?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFormData();
                            }
                        });
                builder.show();
            }
        });
    }

    private void uploadFormData(){
        ArrayList<Integer> scoreList = new ArrayList<>(10);
        scoreList.add(r1.getRightIndex());
        scoreList.add(r2.getRightIndex());
        scoreList.add(r3.getRightIndex());
        scoreList.add(r4.getRightIndex());
        scoreList.add(r5.getRightIndex());
        if(formType != FORM_TYPE_PRACS){
            scoreList.add(r6.getRightIndex());
            scoreList.add(r7.getRightIndex());
            scoreList.add(r8.getRightIndex());
            scoreList.add(r9.getRightIndex());
        }

        String commentString = String.valueOf(commentET.getText());
        String feedbackStr = String.valueOf(scoreList);
        String feedbackInts = feedbackStr.substring(1, feedbackStr.length()-1);

        JSONObject json = new JSONObject();
        try {
            json.put("name", teacherName);
            json.put("subject", subject);
            json.put("feedback", feedbackInts);
            json.put("comments", commentString);
            json.put("isPractical", (formType == FORM_TYPE_PRACS)?"True":"False");
            json.put("division", division);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uploadToServer("http://10.120.110.161:8000/save-feedback/", String.valueOf(json));
        preferences.edit().putBoolean(subject + teacherName, true).apply();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clear_btt_menu, menu);
        return true;
    }

    public void clear(){
        r1.setSeekPinByIndex(2);
        r2.setSeekPinByIndex(2);
        r3.setSeekPinByIndex(2);
        r4.setSeekPinByIndex(2);
        r5.setSeekPinByIndex(2);
        if(formType != FORM_TYPE_PRACS){
            r6.setSeekPinByIndex(2);
            r7.setSeekPinByIndex(2);
            r8.setSeekPinByIndex(2);
            r9.setSeekPinByIndex(2);
        }

        commentET.setText("");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear:
                clear();
                return true;
        }
        return false;
    }

    public void uploadToServer(final String query, final String json) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params)  {
                try {
                    URL url = new URL(query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    OutputStream os = conn.getOutputStream();
                    os.write(json.getBytes("UTF-8"));
                    os.close();

                    int status = conn.getResponseCode();
                    Log.e("HttpStatus", String.valueOf(status));

                    // read the response
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String result = convertStreamToString(in);
                    in.close();

                    conn.disconnect();

                    Log.e("HttpResult", result);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
