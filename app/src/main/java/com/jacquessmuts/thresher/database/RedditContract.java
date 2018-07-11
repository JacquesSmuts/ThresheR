package com.jacquessmuts.thresher.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */
public class RedditContract {

    public static final String CONTENT_AUTHORITY = "com.jacquessmuts.thresher";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for this app.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SUBMISSION = "submission";

    /* Inner class that defines the table contents of the weather table */
    public static final class RedditPostsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SUBMISSION)
                .build();

        /* Used internally as the name of our movies table. */
        public static final String TABLE_NAME = "reddit_posts";

        public static final String COLUMN_REDDIT_POST_ID = "reddit_post_id";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_PERMALINK = "permalink";
        public static final String COLUMN_SELF_TEXT = "selftext";
        public static final String COLUMN_FULLNAME = "fullname";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_NSFW = "adult";
        public static final String COLUMN_TIME_CREATED = "time_created";
        public static final String COLUMN_TIME_MODIFIED = "time_modified";

        public static String getAllSubmissions() {
            return RedditPostsEntry.COLUMN_REDDIT_POST_ID + " NOT NULL";//" >= 0";
        }

        public static String getRedditPostById(String id){
            return RedditPostsEntry.COLUMN_REDDIT_POST_ID + " = " + id;
        }
    }
}