package com.jacquessmuts.thresher.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.RedditPostAdapter;
import com.jacquessmuts.thresher.ThresherApp;
import com.jacquessmuts.thresher.database.DbHelper;
import com.jacquessmuts.thresher.database.RedditContract;
import com.jacquessmuts.thresher.eventbusses.RedditPostSelectedBus;
import com.jacquessmuts.thresher.models.RedditPost;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * An activity representing a list of Submissions. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RedditPostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RedditPostListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static final String LOG_TAG = RedditPostListActivity.class.getSimpleName();

    private RedditPostAdapter submissionListAdapter;

    private Cursor cursor;

    public CompositeDisposable eventDisposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        getSupportLoaderManager().initLoader(DbHelper.ID_SUBMISSIONS_LOADER, null, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        if (findViewById(R.id.submission_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.submission_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView, null);

        getFrontPage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventDisposables.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        eventDisposables.add(RedditPostSelectedBus.getInstance().listen()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RedditPost>() {
                    @Override
                    public void onNext(RedditPost redditPost) {
                        Log.i(LOG_TAG, "clicked on item " + redditPost.toString());
                        startActivity(RedditPostDetailActivity.getIntent(RedditPostListActivity.this, redditPost));
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                }));

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {

            case DbHelper.ID_SUBMISSIONS_LOADER:
                Uri submissionsQuery = RedditContract.RedditPostsEntry.CONTENT_URI;

                String selection = RedditContract.RedditPostsEntry.getAllSubmissions();

                return new CursorLoader(this,
                        submissionsQuery,
                        DbHelper.MAIN_SUBMISSIONS_PROJECTION,
                        selection,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursor = data;
        //if (sortingOption == Server.SortingOption.FAVORITE) {
        //getData(1);
        //if (scrollPosition > 0) {
        //    layoutManager.scrollToPosition(scrollPosition);
        //    scrollPosition = -1;
        //}
        //}
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        submissionListAdapter.swapCursor(null);
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<RedditPost> redditPosts) {
        submissionListAdapter = new RedditPostAdapter(this, redditPosts);
        submissionListAdapter.swapCursor(cursor);
        recyclerView.setAdapter(submissionListAdapter);
    }

    private void getFrontPage(){
        setLoading(true);
        new DownloadPageTask(new WeakReference<>(this)).execute("stringsss");
    }

    private void insertDBValues(List<RedditPost> redditPosts){
        ArrayList<ContentValues> submissionsArrayList = new ArrayList<>();
        for (RedditPost redditPost : redditPosts){
            ContentValues contentValues = new ContentValues();
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_REDDIT_POST_ID, redditPost.getId());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_TITLE, redditPost.getTitle());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_THUMBNAIL, redditPost.getThumbnail());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_SCORE, redditPost.getScore());
            submissionsArrayList.add(contentValues);
        }
        ContentResolver contentResolver = getContentResolver();
        contentResolver.bulkInsert(RedditContract.RedditPostsEntry.CONTENT_URI,
                submissionsArrayList.toArray(new ContentValues[submissionsArrayList.size()]));

        getSupportLoaderManager().initLoader(DbHelper.ID_SUBMISSIONS_LOADER, null, this);
    }

    private void updatePage(List<RedditPost> redditPosts){
        View recyclerView = findViewById(R.id.submission_list);
        setupRecyclerView((RecyclerView) recyclerView, redditPosts);
    }

    private void setLoading(boolean isLoading){
        //TODO: make progress bar (in)visible
    }


    //TODO: Replace with RxJava
    private static class DownloadPageTask extends AsyncTask<String, List<RedditPost>, List<RedditPost>> {
        private final WeakReference<RedditPostListActivity> activity;

        DownloadPageTask(WeakReference<RedditPostListActivity> activity) {
            this.activity = activity;
        }

        @Override
        protected List<RedditPost> doInBackground(String... usernames) {
            RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();
            DefaultPaginator<Submission> frontPage = redditClient.frontPage()
                    .sorting(SubredditSort.TOP)
                    .timePeriod(TimePeriod.DAY)
                    .limit(30)
                    .build();

            Listing<Submission> submissions = frontPage.next();
            for (Submission s : submissions) {
                System.out.println(s.getTitle());
            }

            return RedditPost.fromSubmission(submissions);
        }

        @Override
        protected void onPostExecute(List<RedditPost> redditPosts) {
            RedditPostListActivity activity = this.activity.get();

            if (activity != null) {
                activity.insertDBValues(redditPosts);
                activity.updatePage(redditPosts);
            }
        }
    }
}
