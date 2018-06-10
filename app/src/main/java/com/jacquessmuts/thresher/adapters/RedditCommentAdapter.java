package com.jacquessmuts.thresher.adapters;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.utilities.GenericUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jacques Smuts on 4/21/2018.
 * This is the post detail adapter, which handles the list of reddit comments
 */

public class RedditCommentAdapter
        extends RecyclerView.Adapter<RedditCommentAdapter.ViewHolder> {

    private final Activity parentActivity;
    private List<RedditComment> redditComments;

//    private Cursor cursor;

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

        RedditComment redditComment = null;

//        if (cursor != null && !cursor.isClosed() && cursor.getCount() > position){
//            cursor.moveToPosition(position);
//            //TODO: show correct data from cursor instead of submission
//
//            redditPost = new RedditPost();
//
//            String[] columnNames = cursor.getColumnNames();
//            for (String name: columnNames) {
//                Log.i("adapter", name);
//            }
//
//            redditPost.setId(cursor.getString(DbHelper.INDEX_SUBMISSION_ID));
//            redditPost.setTitle(cursor.getString(DbHelper.INDEX_TITLE));
//            redditPost.setThumbnail(cursor.getString(DbHelper.INDEX_THUMBNAIL_PATH));
//            redditPost.setScore(cursor.getInt(DbHelper.INDEX_SCORE));
//        } else
        if (redditComments != null && redditComments.size() >= position) {
            redditComment = redditComments.get(position);
        }

        if (redditComment == null || redditComment.getBody() == null || redditComment.getBody().isEmpty())
            return;

        holder.textInfo.setText(redditComment.getAuthor());
        holder.textBody.setText(redditComment.getBody());
        holder.textScore.setText(String.valueOf(redditComment.getScore()));

        int marginLeft = (int) GenericUtils.convertDpToPixel(parentActivity, 8 * (redditComment.getDepth()-1));
        int marginOthers = (int) GenericUtils.convertDpToPixel(parentActivity, 1);;
        int marginTop = (redditComment.getDepth() > 1) ? 0 : marginOthers;
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
        layoutParams.setMargins(marginLeft, marginTop, marginOthers, marginOthers);

        holder.itemView.setTag(redditComment);

    }

    @Override
    public int getItemCount() {
        int count = 0;
//        if (cursor != null && cursor.getCount() > 0){
//            count = cursor.getCount();
//        } else
        if (redditComments != null){
            count = redditComments.size();
        }
        return count;
    }

    public void setRedditComments(List<RedditComment> newComments){
        redditComments = newComments;
        notifyDataSetChanged();
    }

//    public void swapCursor(Cursor newCursor) {
//        cursor = newCursor;
//        notifyDataSetChanged();
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_card_view) CardView cardView;
        @BindView(R.id.constraintlayout_comments) ConstraintLayout layout;
        @BindView(R.id.text_info) TextView textInfo;
        @BindView(R.id.text_body) TextView textBody;
        @BindView(R.id.text_score) TextView textScore;
        //TODO: add upvote/downvote buttons

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

