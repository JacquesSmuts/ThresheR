package com.jacquessmuts.thresher;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jacques Smuts on 2/19/2018.
 */

public class SharedPreferenceHelper {

    private static final String PREF_NAME = "com.jacquessmuts.thresher";
    private static final String AUTH_TOKEN = PREF_NAME + "auth_token";

    private static SharedPreferenceHelper mSharedPreferenceHelper;
    private SharedPreferences mSharedPreferences;

    public static SharedPreferenceHelper getInstance(Context context) {
        if (mSharedPreferenceHelper == null) {
            mSharedPreferenceHelper = new SharedPreferenceHelper();
            mSharedPreferenceHelper.mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }

        return mSharedPreferenceHelper;
    }

    public String getAuthToken(){
        return mSharedPreferenceHelper.mSharedPreferences.getString(AUTH_TOKEN, "");
    }

    public void setAuthToken(String authToken){
        mSharedPreferenceHelper.mSharedPreferences.edit().putString(AUTH_TOKEN, authToken).commit();
    }

}
