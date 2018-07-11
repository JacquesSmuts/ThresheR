package com.jacquessmuts.thresher.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jacques Smuts on 5/22/2018
 */
public class DbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    private static final String DATABASE_NAME = "thresher.db";

    private static final int DATABASE_VERSION = 1;

    //ID for a loader
    public static final int ID_SUBMISSIONS_LOADER = 42;

    /*
     * The columns of data that we are interested in displaying within the list
     */
    public static final String[] MAIN_SUBMISSIONS_PROJECTION = {
            RedditContract.RedditPostsEntry.COLUMN_REDDIT_POST_ID,
            RedditContract.RedditPostsEntry.COLUMN_TITLE,
            RedditContract.RedditPostsEntry.COLUMN_THUMBNAIL,
            RedditContract.RedditPostsEntry.COLUMN_SCORE,
            RedditContract.RedditPostsEntry.COLUMN_AUTHOR,
            RedditContract.RedditPostsEntry.COLUMN_PERMALINK,
            RedditContract.RedditPostsEntry.COLUMN_SELF_TEXT,
            RedditContract.RedditPostsEntry.COLUMN_FULLNAME,
            RedditContract.RedditPostsEntry.COLUMN_VOTE,
            RedditContract.RedditPostsEntry.COLUMN_NSFW
    };


    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our data.
         */
        final String SQL_CREATE_SUBMISSION_TABLE =

                "CREATE TABLE " + RedditContract.RedditPostsEntry.TABLE_NAME + " (" +

                        /*
                         * RedditPostsEntry did not explicitly declare a column called "_ID". However,
                         * RedditPostsEntry implements the interface, "BaseColumns", which does have a field
                         * named "_ID". We use that here to designate our table's primary key.
                         */
                        //TODO: handle replace/update conflicts better, so as not to overwrite anything
                        RedditContract.RedditPostsEntry.COLUMN_REDDIT_POST_ID + " TEXT PRIMARY KEY NOT NULL ON CONFLICT REPLACE, " +
                        RedditContract.RedditPostsEntry.COLUMN_TITLE   + " TEXT, " +
                        RedditContract.RedditPostsEntry.COLUMN_THUMBNAIL + " TEXT," +
                        RedditContract.RedditPostsEntry.COLUMN_SCORE + " TEXT, " +
                        RedditContract.RedditPostsEntry.COLUMN_AUTHOR   + " TEXT, " +
                        RedditContract.RedditPostsEntry.COLUMN_PERMALINK   + " TEXT, " +
                        RedditContract.RedditPostsEntry.COLUMN_SELF_TEXT   + " TEXT, " +
                        RedditContract.RedditPostsEntry.COLUMN_FULLNAME   + " TEXT, " +
                        RedditContract.RedditPostsEntry.COLUMN_VOTE   + " INTEGER, " +
                        RedditContract.RedditPostsEntry.COLUMN_NSFW    + " BOOLEAN);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_SUBMISSION_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RedditContract.RedditPostsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
