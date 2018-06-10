package com.jacquessmuts.thresher.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.models.RedditPost;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Submission detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RedditPostListActivity}.
 */
public class RedditPostDetailActivity extends AppCompatActivity {

    public static final String KEY_REDDIT_POST = "reddit_post";

    private String redditPostId;
    private RedditPost redditPost;

    @BindView(R.id.detail_toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;

    public static Intent getIntent(Context context, RedditPost redditPost){
        Intent intent = new Intent (context, RedditPostDetailActivity.class);
        //intent.putExtra(KEY_REDDIT_POST, GenericUtils.serializeSubmission(redditPost));
        intent.putExtra(KEY_REDDIT_POST, redditPost);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        );

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            //submission = GenericUtils.deserializeSubmission(intent.getStringExtra(KEY_REDDIT_POST));
            redditPost = intent.getParcelableExtra(KEY_REDDIT_POST);
            redditPostId = redditPost.getId();
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(KEY_REDDIT_POST, redditPost);
            SubmissionDetailFragment fragment = new SubmissionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.submission_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, RedditPostListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
