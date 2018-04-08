package com.jacquessmuts.thresher.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.ThresherApp;

import net.dean.jraw.models.PersistedAuthData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TreeMap;

public class SplashActivity extends AppCompatActivity {

    private static final int REQ_CODE_LOGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        doAuthCheck();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user could have pressed the back button before authorizing our app, make sure we have
        // an authenticated user before starting the UserOverviewActivity.
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            goToHomeActivity();
        }
    }

    private void doAuthCheck(){
        //check if there is at least one user token. For now, only one user per app install
        if (ThresherApp.getTokenStore().size() > 0){

            TreeMap<String, PersistedAuthData> data = new TreeMap<>(ThresherApp.getTokenStore().data());
            ArrayList<String> usernames = new ArrayList<>(data.keySet());
            new ReauthenticationTask(new WeakReference<>(this)).execute(usernames.get(0));
        } else {
            getUserAuth();
        }
    }

    private void getUserAuth(){
        startActivityForResult(new Intent(this, NewUserActivity.class), REQ_CODE_LOGIN);
    }

    private void goToHomeActivity(){
        startActivity(new Intent(this, UserOverviewActivity.class));
        //startActivity(new Intent (this, SubmissionListActivity.class));
    }

    private static class ReauthenticationTask extends AsyncTask<String, Void, Void> {
        private final WeakReference<SplashActivity> activity;

        ReauthenticationTask(WeakReference<SplashActivity> activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(String... usernames) {
            ThresherApp.getAccountHelper().switchToUser(usernames[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Activity activity = this.activity.get();

            if (activity != null) {
                activity.startActivity(new Intent(activity, UserOverviewActivity.class));
            }
        }
    }
}
