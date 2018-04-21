package com.jacquessmuts.thresher.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.jacquessmuts.thresher.R;

import com.jacquessmuts.thresher.SubmissionRecyclerViewAdapter;
import com.jacquessmuts.thresher.ThresherApp;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;

import java.lang.ref.WeakReference;

/**
 * An activity representing a list of Submissions. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SubmissionDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SubmissionListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, Listing<Submission> submissions) {
        recyclerView.setAdapter(new SubmissionRecyclerViewAdapter(this, submissions));
    }

    private void getFrontPage(){
        new DownloadPageTask(new WeakReference<>(this)).execute("stringsss");
    }

    private void updatePage(Listing<Submission> submissions){
        View recyclerView = findViewById(R.id.submission_list);
        setupRecyclerView((RecyclerView) recyclerView, submissions);
    }

    private static class DownloadPageTask extends AsyncTask<String, Listing<Submission>, Listing<Submission>> {
        private final WeakReference<SubmissionListActivity> activity;

        DownloadPageTask(WeakReference<SubmissionListActivity> activity) {
            this.activity = activity;
        }

        @Override
        protected Listing<Submission> doInBackground(String... usernames) {
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
            return submissions;
        }

        @Override
        protected void onPostExecute(Listing<Submission> submissions) {
            SubmissionListActivity activity = this.activity.get();

            if (activity != null) {
                activity.updatePage(submissions);
            }
        }
    }
}
