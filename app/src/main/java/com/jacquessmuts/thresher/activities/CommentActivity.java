package com.jacquessmuts.thresher.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.models.RedditPost;
import com.jacquessmuts.thresher.utilities.GenericUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

public class CommentActivity extends AppCompatActivity {

    public static final String KEY_REDDIT_COMMENT = "reddit_comment";
    public static final String KEY_REDDIT_POST = "reddit_post";

    @State
    String redditPostId;
    private RedditPost redditPost;
    private RedditComment redditComment;

    @BindView(R.id.textViewUserDetails) TextView textViewUserDetails;
    @BindView(R.id.textViewTimeStamp) TextView textViewTimeStamp;
    @BindView(R.id.imageViewPreview) ImageView imageViewPreview;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.editText) TextView editText;
    @BindView(R.id.textViewComment) TextView textViewComment;

    public static Intent getIntent(Context context, RedditPost post){
        Intent intent = new Intent (context, CommentActivity.class);
        intent.putExtra(KEY_REDDIT_POST, post);
        return intent;
    }

    public static Intent getIntent(Context context, RedditPost post, RedditComment comment){
        Intent intent = new Intent (context, CommentActivity.class);
        intent.putExtra(KEY_REDDIT_POST, post);
        intent.putExtra(KEY_REDDIT_COMMENT, comment);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Icepick.restoreInstanceState(this, savedInstanceState);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            redditPost = intent.getParcelableExtra(KEY_REDDIT_POST);
            redditPostId = redditPost.getId();
            if (intent.hasExtra(KEY_REDDIT_COMMENT)) {
                redditComment = intent.getParcelableExtra(KEY_REDDIT_COMMENT);
            }
        }

        populateViews();
    }

    private void populateViews(){

        Picasso.with(this).load(redditPost.getThumbnail()).into(imageViewPreview);

        if (redditComment != null){
            textViewUserDetails.setText(getString(R.string.username, redditComment.getAuthor()));
            textViewTimeStamp.setText(GenericUtils.convertTimestampToTimeSince(redditComment.getCreated_utc().getTime()));
            textViewComment.setText(redditComment.getBody());
        } else {

            textViewUserDetails.setText(getString(R.string.username, redditPost.getAuthor()));
            textViewTimeStamp.setText(GenericUtils.convertTimestampToTimeSince(redditPost.getCreated_utc().getTime()));
            if (redditPost.isSelfPost()) {
                textViewComment.setText(redditPost.getSelfText());
            }
        }



        fab.setOnClickListener(v -> {
            makeComment();
        });
    }

    private void makeComment(){
        Timber.i("Making comment: " + editText.getText());
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

}
