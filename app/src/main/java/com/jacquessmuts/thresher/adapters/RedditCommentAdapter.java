package com.jacquessmuts.thresher.adapters;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.eventbusses.RedditSubmissionVotedBus;
import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.utilities.GenericUtils;

import net.dean.jraw.models.VoteDirection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Jacques Smuts on 4/21/2018.
 * This is the post detail adapter, which handles the list of reddit comments
 */

public class RedditCommentAdapter
        extends RecyclerView.Adapter<RedditCommentAdapter.ViewHolder> {

    private final Activity parentActivity;
    private List<RedditComment> redditComments;

    public RedditCommentAdapter(Activity parent, List<RedditComment> items) {
        redditComments = items;
        parentActivity = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_comment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Timber.d("OnBindViewHolder " + position);
        RedditComment redditComment = null;

        if (redditComments != null && redditComments.size() >= position) {
            redditComment = redditComments.get(position);
        }

        if (redditComment == null || redditComment.getBody() == null || redditComment.getBody().isEmpty())
            return;

        holder.textInfo.setText(redditComment.getAuthor());
        holder.textBody.setText(redditComment.getBody());
        holder.textScore.setText(String.valueOf(redditComment.getScore()));

        holder.buttonUpvote.setVisibility(View.VISIBLE);
        holder.buttonDownvote.setVisibility(View.VISIBLE);
        switch (redditComment.getVote()){
            case UP:
                holder.buttonUpvote.setVisibility(View.INVISIBLE);
                break;
            case DOWN:
                holder.buttonDownvote.setVisibility(View.INVISIBLE);
                break;
        }

        int marginLeft = (int) GenericUtils.convertDpToPixel(parentActivity, 8 * (redditComment.getDepth()-1));
        int marginOthers = (int) GenericUtils.convertDpToPixel(parentActivity, 1);;
        int marginTop = (redditComment.getDepth() > 1) ? 0 : marginOthers;
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
        layoutParams.setMargins(marginLeft, marginTop, marginOthers, marginOthers);

        holder.itemView.setTag(redditComment);

        RedditComment finalRedditComment = redditComment;
        holder.buttonUpvote.setOnClickListener(v -> {
            RedditSubmissionVotedBus.getInstance().onNext(
                    new RedditSubmissionVotedBus.VoteAction(finalRedditComment, VoteDirection.UP));
        });

        holder.buttonDownvote.setOnClickListener(v -> {
            RedditSubmissionVotedBus.getInstance().onNext(
                    new RedditSubmissionVotedBus.VoteAction(finalRedditComment, VoteDirection.DOWN));
        });

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (redditComments != null){
            count = redditComments.size();
        }
        return count;
    }

    public void setRedditComments(List<RedditComment> newComments){
        redditComments = newComments;
        notifyDataSetChanged();
    }

    public void commentUpdated(RedditComment redditComment){
        if (redditComments.contains(redditComment)){
            RedditComment postToUpdate = redditComments.get(redditComments.indexOf(redditComment));
            postToUpdate.setVote(redditComment.getVote());
        }

        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_card_view) CardView cardView;
        @BindView(R.id.constraintlayout_comments) ConstraintLayout layout;
        @BindView(R.id.text_info) TextView textInfo;
        @BindView(R.id.text_body) TextView textBody;
        @BindView(R.id.text_score) TextView textScore;
        @BindView(R.id.button_upvote) ImageButton buttonUpvote;
        @BindView(R.id.button_downvote) ImageButton buttonDownvote;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

