package com.jacquessmuts.thresher.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.ThresherApp;

public class SplashActivity extends AppCompatActivity {

    private static final int REQ_CODE_LOGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        doAuthCheck();
    }

    private void doAuthCheck(){
        boolean hasNoTokens = ThresherApp.getTokenStore().size() == 0;
        if (hasNoTokens){
            getUserAuth();
        }
    }

    private void getUserAuth(){
        startActivityForResult(new Intent(this, NewUserActivity.class), REQ_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user could have pressed the back button before authorizing our app, make sure we have
        // an authenticated user before starting the UserOverviewActivity.
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            //TODO: startActivity(new Intent(this, UserOverviewActivity.class));
        }
    }
}
