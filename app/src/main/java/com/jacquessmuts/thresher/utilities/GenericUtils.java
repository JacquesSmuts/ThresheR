package com.jacquessmuts.thresher.utilities;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.databind.ModelAdapterFactory;
import net.dean.jraw.models.Submission;

import java.io.IOException;

/**
 * Created by Jacques Smuts on 5/20/2018.
 * All of the utility functions that have not found a specific class yet go here.
 */
public class GenericUtils {


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
}
