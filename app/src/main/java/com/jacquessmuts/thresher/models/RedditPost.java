package com.jacquessmuts.thresher.models;

import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;

import net.dean.jraw.databind.UnixTime;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.EmbeddedMedia;
import net.dean.jraw.models.SubmissionPreview;
import net.dean.jraw.models.VoteDirection;

import java.util.Date;

/**
 * Created by Jacques Smuts on 4/15/2018.
 * This is a model based on the JRAW Submission.class model.
 * Will probably use this model instead of dealing with JRAW directly.
 */
public class RedditPost {

    boolean isNsfw;
    String author;
    @UnixTime Date created_utc;
    @UnixTime Date edited;
    String domain;
    EmbeddedMedia embeddedMedia;
    String id;
    String name;
    boolean isScoreHidden;
    boolean isSelfPost;
    boolean isLocked;
    String permalink;
    String postHint;
    SubmissionPreview preview;
    String selfText;
    boolean isSpam;
    boolean isSpoiler;
    String subreddit;
    String subredditFullName;
    CommentSort suggestedSort;
    String thumbnail;
    String title;
    String url;
    boolean isRemoved;
    VoteDirection vote; //aka likes
    Integer commentCount;



}
