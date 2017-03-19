package api.feedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

import static api.feedback.LoginActivity.SP_LOGIN_ID;
import static api.feedback.LoginActivity.SP_LOGIN_LOGGED_IN_STATE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressDialog progressDialog = new ProgressDialog(this);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.login_logging_in));
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                handleLogCheck();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPreExecute();
                progressDialog.dismiss();
            }
        }.execute();

        SharedPreferences prefs = getSharedPreferences(SP_LOGIN_ID, Context.MODE_PRIVATE);
        RecyclerView rv = (RecyclerView) findViewById(R.id.subject_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        String div = prefs.getString(LoginActivity.SP_LOGIN_CLASS, "");
        rv.addItemDecoration(new SpacingDecoration(4));
        rv.setAdapter(new SubjectAdapter(this, getData(div)));
    }

    private ArrayList<Subject> getData(String div){
        ArrayList<Subject> subjects = new ArrayList<>();
        if(Objects.equals(div, "SE-A")){
            subjects.add(new Subject("Saumitra Bose", "DBMS", "SE-A", false));
            subjects.add(new Subject("Cyprien Dcunha", "CG", "SE-A", false));
            subjects.add(new Subject("Cyprien Dcunha", "CG", "SE-A", true));
            subjects.add(new Subject("Jitendra Kumhar", "COA", "SE-A", false));
            subjects.add(new Subject("Saumitra Bose", "DBMS", "SE-A", true));
            subjects.add(new Subject("Ketav Bhatt", "Maths", "SE-A", true));
            subjects.add(new Subject("Jitendra Kumhar", "AOA", "SE-A", false));
        }
        else if(Objects.equals(div, "SE-B")){
            subjects.add(new Subject("Saumitra Bose", "DBMS", "SE-B", false));
            subjects.add(new Subject("Chirag", "CG", "SE-B", false));
            subjects.add(new Subject("Miloni", "CG", "SE-B", true));
            subjects.add(new Subject("Maitri", "COA", "SE-B", false));
            subjects.add(new Subject("Saumitra", "DBMS", "SE-B", true));
            subjects.add(new Subject("Ramesh", "Maths", "SE-B", true));
            subjects.add(new Subject("Cyprien Dcunha", "AOA", "SE-B", false));
        }
        else {

            subjects.add(new Subject("Saumitra Bose", "DBMS", "SE-A", false));
            subjects.add(new Subject("Jitendra Kumhar", "COA", "SE-A", false));
            subjects.add(new Subject("Ankit", "CG", "SE-A", true));
            subjects.add(new Subject("Jigar", "CG", "SE-A", false));
            subjects.add(new Subject("Saumitra Bose", "DBMS", "SE-A", true));
            subjects.add(new Subject("Anand", "Maths", "SE-A", true));
            subjects.add(new Subject("Zahan", "Maths", "SE-A", false));
        }
        return subjects;
    }

    private void handleLogCheck(){
        SharedPreferences prefs = getSharedPreferences(SP_LOGIN_ID, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(SP_LOGIN_LOGGED_IN_STATE, false);

        if(!isLoggedIn){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private static class SpacingDecoration extends RecyclerView.ItemDecoration {
        private int spacing;

        SpacingDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = spacing;
            outRect.top = spacing;

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences prefs = getSharedPreferences(SP_LOGIN_ID, Context.MODE_PRIVATE);
                prefs.edit().putBoolean(SP_LOGIN_LOGGED_IN_STATE, false).apply();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
        }
        return false;
    }
}


