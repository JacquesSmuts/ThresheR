package com.jacquessmuts.thresher.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.jacquessmuts.thresher.R;
import com.squareup.moshi.JsonAdapter;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.Submission;

import java.io.IOException;

/**
 * Created by Jacques Smuts on 5/20/2018.
 * All of the utility functions that have not found a specific class yet go here.
 */
public class GenericUtils {

    public static boolean isTablet(Resources resources){
        return resources.getBoolean(R.bool.isTablet);
    }

    public static String serializeSubmission(Submission submission){
        JsonAdapter<Submission> jsonAdapter = JrawUtils.moshi.adapter(Submission.class).serializeNulls();
        return jsonAdapter.toJson(submission);
    }

    public static Submission deserializeSubmission(String submissionString){
        JsonAdapter<Submission> jsonAdapter = JrawUtils.moshi.adapter(Submission.class).serializeNulls();
        Submission deserialized = null;

        try {
            deserialized = jsonAdapter.fromJson(submissionString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return deserialized;
    }

    public static String convertTimestampToTimeSince(long timeStampInMilliSeconds){
        long currentTime =  System.currentTimeMillis();
        int differenceInSeconds = (int) ((currentTime - timeStampInMilliSeconds)/1000);
        int differenceInMinutes = differenceInSeconds/60;
        int differenceInHours = differenceInMinutes/60;
        int differenceInDays = differenceInHours/24;

        String toReturn = "";
        if (differenceInSeconds < 60){
            toReturn = differenceInSeconds + " seconds ago";
        } else if (differenceInMinutes < 60) {
            toReturn = differenceInMinutes + " minutes ago";
        } else if (differenceInHours < 24) {
            toReturn = differenceInHours + " hours ago";
        } else {
            toReturn = differenceInDays + " days ago";
        }

        return toReturn;

    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(Context context, float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
