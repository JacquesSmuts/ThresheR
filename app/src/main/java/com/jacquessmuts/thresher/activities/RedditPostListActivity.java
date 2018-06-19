package com.jacquessmuts.thresher.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.ThresherApp;
import com.jacquessmuts.thresher.adapters.RedditPostAdapter;
import com.jacquessmuts.thresher.database.DbHelper;
import com.jacquessmuts.thresher.database.RedditContract;
import com.jacquessmuts.thresher.eventbusses.RedditPostSelectedBus;
import com.jacquessmuts.thresher.eventbusses.RedditPostVotedBus;
import com.jacquessmuts.thresher.models.RedditPost;
import com.jacquessmuts.thresher.utilities.GenericUtils;
import com.jacquessmuts.thresher.utilities.JrawConversionUtils;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.models.VoteDirection;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
    private boolean isTablet;
    public static final String LOG_TAG = RedditPostListActivity.class.getSimpleName();

    private RedditPostAdapter submissionListAdapter;

    //TODO: private PageFilter currentlySelectedFilter;

    private Cursor cursor;

    public CompositeDisposable eventDisposables = new CompositeDisposable();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.submission_list) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("OnCreate Beginning");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        getSupportLoaderManager().initLoader(DbHelper.ID_SUBMISSIONS_LOADER, null, this);

        isTablet = GenericUtils.isTablet(getResources());

        assert recyclerView != null;
        setupRecyclerView(recyclerView, null);

        getFrontPage();
        Timber.d("OnCreate End");
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
                        Timber.i( "clicked on item " + redditPost.toString());
                        startActivity(RedditPostDetailActivity.getIntent(RedditPostListActivity.this, redditPost));
                    }
                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                }));

        eventDisposables.add(RedditPostVotedBus.getInstance().listen()
                .observeOn(Schedulers.computation())
                .map(voteAction -> {
                    RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();
                    try {
                        switch (voteAction.getVoteDirection()) {
                            case UP:
                                redditClient.submission(voteAction.getRedditPost().getId()).upvote();
                                break;
                            case DOWN:
                                redditClient.submission(voteAction.getRedditPost().getId()).downvote();
                                break;
                        }
                    } catch (ApiException e){
                        //TODO the JRAW client currently has this issue so upvotes sometimes don't work. Make more robust.
                        Timber.w(e.getMessage());
                    }
                    return voteAction;
                } )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RedditPostVotedBus.VoteAction>() {
                    @Override
                    public void onNext(RedditPostVotedBus.VoteAction voteAction) {
                        Timber.d("voted on item " + voteAction.toString());
                        handleLocalVote(voteAction);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Timber.e("error");
                    }
                    @Override
                    public void onComplete() {
                        Timber.v("complete");
                    }
                }));

    }

    private void handleLocalVote(RedditPostVotedBus.VoteAction voteAction){
        RedditPost redditPost = voteAction.getRedditPost();

        if (redditPost.getVote() == null) redditPost.setVote(VoteDirection.NONE);

        switch (voteAction.getVoteDirection()) {
            case UP:
                switch (redditPost.getVote()){
                    case UP:
                    case NONE:
                        redditPost.setVote(VoteDirection.UP);
                        break;
                    case DOWN:
                        redditPost.setVote(VoteDirection.NONE);
                        break;
                }
                break;
            case DOWN:
                switch (redditPost.getVote()){
                    case UP:
                        redditPost.setVote(VoteDirection.NONE);
                        break;
                    case NONE:
                    case DOWN:
                        redditPost.setVote(VoteDirection.DOWN);
                        break;
                }
                break;
        }

        //TODO: update database entry and refresh all from database

        submissionListAdapter.postUpdated(redditPost);
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

        // TODO: load data into memory using RxJava and applying filter
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //nothing to do here?
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<RedditPost> redditPosts) {
        submissionListAdapter = new RedditPostAdapter(this, redditPosts);
        recyclerView.setAdapter(submissionListAdapter);
    }

    private void getFrontPage(){
        setLoading(true);

        Observable.fromCallable(this::downloadFrontPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((redditPosts) -> {
                    insertDBValues(redditPosts);
                    updatePage(redditPosts);
                    setLoading(false);
                });
    }

    private List<RedditPost> downloadFrontPage(){
        RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();
        DefaultPaginator<Submission> frontPage = redditClient.frontPage()
                .sorting(SubredditSort.TOP)
                .timePeriod(TimePeriod.DAY)
                .limit(30)
                .build();
        Listing<Submission> submissions = frontPage.next();
        for (Submission s : submissions) {
            Timber.v(s.getTitle());
        }
        return JrawConversionUtils.getRedditPosts(submissions);
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
        setupRecyclerView(recyclerView, redditPosts);
    }

    private void setLoading(boolean isLoading){
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
