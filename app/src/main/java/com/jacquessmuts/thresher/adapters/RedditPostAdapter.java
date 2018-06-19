package com.jacquessmuts.thresher.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.activities.RedditPostListActivity;
import com.jacquessmuts.thresher.eventbusses.RedditPostSelectedBus;
import com.jacquessmuts.thresher.eventbusses.RedditPostVotedBus;
import com.jacquessmuts.thresher.models.RedditPost;
import com.squareup.picasso.Picasso;

import net.dean.jraw.models.VoteDirection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Jacques Smuts on 4/21/2018.
 * This is the main recyclerview, which handles the list of reddit posts, aka redditPosts
 */

public class RedditPostAdapter
        extends RecyclerView.Adapter<RedditPostAdapter.ViewHolder> {

    private final RedditPostListActivity parentActivity;
    private final List<RedditPost> redditPosts;

    public RedditPostAdapter(RedditPostListActivity parent,
                             List<RedditPost> items) {
        redditPosts = items;
        parentActivity = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_submission_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Timber.d("OnBindViewHolder " + position);
        RedditPost redditPost = null;

        if (redditPosts != null && redditPosts.size() >= position) {
            redditPost = redditPosts.get(position);
        }

        if (redditPost == null || redditPost.getTitle() == null || redditPost.getTitle().isEmpty())
            return;

        String thumbnailPath = redditPost.getThumbnail();
        if (thumbnailPath == null || thumbnailPath.isEmpty() || thumbnailPath.equals("default")) {
            holder.imagePreview.setVisibility(View.GONE);
        } else {
            Picasso.with(parentActivity).load(thumbnailPath).into(holder.imagePreview);
            holder.imagePreview.setVisibility(View.VISIBLE);
        }

        holder.textTitle.setText(redditPost.getTitle());
        holder.textScore.setText(String.valueOf(redditPost.getScore()));

        //TODO: holder.imageUpvote(redditPost.getVote()) blabla

        holder.itemView.setTag(redditPost);
        RedditPost finalRedditPost = redditPost;

        holder.itemView.setOnClickListener(v ->
                RedditPostSelectedBus.getInstance().onNext(finalRedditPost));

        holder.buttonUpvote.setOnClickListener(v -> {
            RedditPostVotedBus.getInstance().onNext(
                    new RedditPostVotedBus.VoteAction(finalRedditPost, VoteDirection.UP));
        });

        holder.buttonDownvote.setOnClickListener(v -> {
            RedditPostVotedBus.getInstance().onNext(
                    new RedditPostVotedBus.VoteAction(finalRedditPost, VoteDirection.DOWN));
        });

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (redditPosts != null){
            count = redditPosts.size();
        }
        return count;
    }

    public void postUpdated(RedditPost redditPost){
        if (redditPosts.contains(redditPost)){
            RedditPost postToUpdate = redditPosts.get(redditPosts.indexOf(redditPost));
            postToUpdate.setVote(redditPost.getVote());
        }

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_main) ImageView imagePreview;
        @BindView(R.id.text_title)  TextView textTitle;
        @BindView(R.id.text_score)  TextView textScore;
        @BindView(R.id.text_info)  TextView textInfo;
        @BindView(R.id.button_upvote) ImageButton buttonUpvote;
        @BindView(R.id.button_downvote) ImageButton buttonDownvote;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

