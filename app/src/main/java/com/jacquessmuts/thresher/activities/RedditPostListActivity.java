package com.jacquessmuts.thresher.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.ThresherApp;
import com.jacquessmuts.thresher.adapters.RedditPostAdapter;
import com.jacquessmuts.thresher.database.DbHelper;
import com.jacquessmuts.thresher.database.RedditContract;
import com.jacquessmuts.thresher.eventbusses.PostSelectedBus;
import com.jacquessmuts.thresher.eventbusses.SubmissionVotedBus;
import com.jacquessmuts.thresher.models.RedditPost;
import com.jacquessmuts.thresher.models.SubmissionSort;
import com.jacquessmuts.thresher.utilities.DbUtils;
import com.jacquessmuts.thresher.utilities.GenericUtils;
import com.jacquessmuts.thresher.utilities.JrawConversionUtils;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.KarmaBySubreddit;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SelfUserReference;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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
public class RedditPostListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean isTablet;

    private RedditPostAdapter submissionListAdapter;

    private CompositeDisposable eventDisposables = new CompositeDisposable();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.submission_list) RecyclerView recyclerView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    TextView textViewUserDetails;
    TextView textViewUsername;

    @State SubmissionSort selectedSubmissionSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("OnCreate Beginning");
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_submission_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        textViewUserDetails = navigationView.getHeaderView(0).findViewById(R.id.textViewUserDetails);
        textViewUsername = navigationView.getHeaderView(0).findViewById(R.id.textViewUsername);

        getSupportLoaderManager().initLoader(DbHelper.ID_SUBMISSIONS_LOADER, null, this);

        isTablet = GenericUtils.isTablet(getResources());

        assert recyclerView != null;
        setupRecyclerView(recyclerView, null);

        getFrontPage();
        getUserDetails();
        Timber.d("OnCreate End");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        SubmissionSort nuSubmissionSort = SubmissionSort.HOT;
        boolean toReturn;
        switch (item.getItemId()) {
            case R.id.menu_sort_hot:
                nuSubmissionSort = SubmissionSort.HOT;
                toReturn = true;
                break;
            case R.id.menu_sort_new:
                nuSubmissionSort = SubmissionSort.NEW;
                toReturn = true;
                break;
            case R.id.menu_sort_rising:
                nuSubmissionSort = SubmissionSort.RISING;
                toReturn = true;
                break;
            case R.id.menu_sort_top:
                nuSubmissionSort = SubmissionSort.TOP;
                toReturn = true;
                break;
            case R.id.menu_sort_controversial:
                nuSubmissionSort = SubmissionSort.CONTROVERSIAL;
                toReturn = true;
                break;
            default:
                toReturn = super.onOptionsItemSelected(item);
                break;
        }

        if (nuSubmissionSort != selectedSubmissionSort){
            selectedSubmissionSort = nuSubmissionSort;
            getFrontPage();
        }

        return toReturn;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //do nothing
        } else if (id == R.id.nav_user) {
            startActivity(new Intent(this, UserOverviewActivity.class));
        } else if (id == R.id.nav_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "You can download the Reddit App here: [link].");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Send To"));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventDisposables.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        eventDisposables.add(PostSelectedBus.getInstance().listen()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( redditPost -> {
                    Timber.i( "clicked on item " + redditPost.toString());
                    startActivity(RedditPostDetailActivity.getIntent(RedditPostListActivity.this, redditPost));
                }, Timber::e));

        eventDisposables.add(SubmissionVotedBus.getInstance().listen()
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
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(voteAction -> {
                    Timber.d("voted on item " + voteAction.toString());
                    RedditPost redditPost = JrawConversionUtils.implementVote(voteAction).getRedditPost();
                    submissionListAdapter.postUpdated(redditPost);
                }, Timber::e));

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        Timber.v("Loader created");
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
        Timber.d("Loader onLoadFinished");

        //go over the cursor and load all results into memory. This can take a few milliseconds if there are a lot of results
        Single.fromCallable(() -> DbUtils.redditPostsFromCursor(data))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(redditPosts -> {
                    Timber.d("RedditPosts loaded from cursor");
                    updatePage(redditPosts);
                }, Timber::e);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Timber.d("loader has reset");
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
                .subscribe(redditPosts -> {
                    insertDBValues(redditPosts);
                    setLoading(false);
                }, Timber::e);
    }

    private List<RedditPost> downloadFrontPage(){

        if (selectedSubmissionSort == null) selectedSubmissionSort = SubmissionSort.HOT;

        RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();
        DefaultPaginator<Submission> frontPage = redditClient.frontPage()
                .sorting(selectedSubmissionSort.getSort())
                .timePeriod(TimePeriod.DAY)
                .limit(30)
                .build();
        Listing<Submission> submissions = frontPage.next();
        for (Submission s : submissions) {
            Timber.v(s.getTitle());
        }
        return JrawConversionUtils.getRedditPosts(submissions);
    }

    private void getUserDetails(){
        Observable.fromCallable(this::downloadUserDetails)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::populateUserDetails, Timber::e);
    }

    private void populateUserDetails(SelfUserReference me){
        textViewUsername.setText(me.getUsername());
        //List<KarmaBySubreddit> karma = me.karma();
        //textViewUserDetails.setText(""+ me.karma());
    }


    private SelfUserReference downloadUserDetails(){

        if (selectedSubmissionSort == null) selectedSubmissionSort = SubmissionSort.HOT;

        RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();
        return redditClient.me();
    }

    private void insertDBValues(List<RedditPost> redditPosts){
        DbUtils.insert(getContentResolver(), redditPosts);
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
