
package com.jacquessmuts.thresher.database;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RedditProvider extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for.
     */
    public static final int CODE_SUBMISSION = 100;
    public static final int CODE_SUBMISSION_WITH_DATE = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private DbHelper openHelper;

    /**
     * @return A UriMatcher that correctly matches the constants for CODE_SUBMISSION and CODE_SUBMISSION_WITH_DATE
     */
    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RedditContract.CONTENT_AUTHORITY;

        /* This URI is content://com.jacques.thresher/submission/ */
        matcher.addURI(authority, RedditContract.PATH_SUBMISSION, CODE_SUBMISSION);

        /*
         * This URI would look something like content://com.jacques.thresher/submission/1472214172
         * The "/#" signifies to the UriMatcher that if PATH_SUBMISSION is followed by ANY number,
         * that it should return the CODE_SUBMISSION_WITH_DATE code
         */
        //matcher.addURI(authority, RedditContract.PATH_SUBMISSION + "/#", CODE_SUBMISSION_WITH_DATE);

        return matcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     *
     * Nontrivial initialization (such as opening, upgrading, and scanning
     * databases) should be deferred until the content provider is used (via {@link #query},
     * {@link #bulkInsert(Uri, ContentValues[])}, etc).
     *
     * Deferred initialization keeps application startup fast, avoids unnecessary work if the
     * provider turns out not to be needed, and stops database errors (such as a full disk) from
     * halting application launch.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since MovieDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        openHelper = new DbHelper(getContext());
        return true;
    }

    /**
     * Handles requests to insert a set of new rows.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case CODE_SUBMISSION:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(RedditContract.RedditPostsEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * Handles query requests from clients.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (uriMatcher.match(uri)) {

            /*
             * When uriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.jacques.thresher/submission/
             *
             * uriMatcher's match method will return the code that indicates to us that we need
             * to return all of the submissions in our submission table.
             *
             * In this case, we want to return a cursor that contains every row of submission data
             * in our submission table.
             */
            case CODE_SUBMISSION: {
                cursor = openHelper.getReadableDatabase().query(
                        RedditContract.RedditPostsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (uriMatcher.match(uri)) {

            case CODE_SUBMISSION:
                numRowsDeleted = openHelper.getWritableDatabase().delete(
                        RedditContract.RedditPostsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    /**
     * Normally, this method handles requests for the MIME type of the data at the
     * given URI. For example, if your app provided images at a particular URI, then you would
     * return an image URI from this method.
     *
     * @param uri the URI to query.
     * @return nothing, but normally a MIME type string, or null if there is no type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implementing getType .");
    }

    /**
     *
     * @param uri    The URI of the insertion request. This must not be null.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be null
     * @return nothing, but normally the URI for the newly inserted item.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();

        long id;
        switch (uriMatcher.match(uri)) {
            case CODE_SUBMISSION:
                db.beginTransaction();
                try {
                    id = db.insertWithOnConflict(
                            RedditContract.RedditPostsEntry.TABLE_NAME,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return getUriForId(id, uri);
        }
        return null;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            return ContentUris.withAppendedId(uri, id);
        }
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //TODO: implement update database if making any changes to the DB after release
        throw new RuntimeException("Update is not implemented yet");
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        openHelper.close();
        super.shutdown();
    }
}