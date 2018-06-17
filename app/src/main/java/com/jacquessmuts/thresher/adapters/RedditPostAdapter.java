package com.jacquessmuts.thresher.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.activities.RedditPostListActivity;
import com.jacquessmuts.thresher.database.DbHelper;
import com.jacquessmuts.thresher.eventbusses.RedditPostSelectedBus;
import com.jacquessmuts.thresher.models.RedditPost;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jacques Smuts on 4/21/2018.
 * This is the main recyclerview, which handles the list of reddit posts, aka redditPosts
 */

public class RedditPostAdapter
        extends RecyclerView.Adapter<RedditPostAdapter.ViewHolder> {

    private final RedditPostListActivity parentActivity;
    private final List<RedditPost> redditPosts;

    //TODO: remove cursor from adapter. Database must be completely handled in Activities
    private Cursor cursor;

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

        RedditPost redditPost = null;

        if (cursor != null && !cursor.isClosed() && cursor.getCount() > position){
            cursor.moveToPosition(position);

            redditPost = new RedditPost();

            String[] columnNames = cursor.getColumnNames();
            for (String name: columnNames) {
                Log.i("adapter", name);
            }

            redditPost.setId(cursor.getString(DbHelper.INDEX_SUBMISSION_ID));
            redditPost.setTitle(cursor.getString(DbHelper.INDEX_TITLE));
            redditPost.setThumbnail(cursor.getString(DbHelper.INDEX_THUMBNAIL_PATH));
            redditPost.setScore(cursor.getInt(DbHelper.INDEX_SCORE));
        }
        else if (redditPosts != null && redditPosts.size() >= position) {
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

        holder.itemView.setTag(redditPost);
        RedditPost finalRedditPost = redditPost;
        holder.itemView.setOnClickListener(v ->
                RedditPostSelectedBus.getInstance().onNext(finalRedditPost));

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null && cursor.getCount() > 0){
            count = cursor.getCount();
        } else if (redditPosts != null){
            count = redditPosts.size();
        }
        return count;
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_main) ImageView imagePreview;
        @BindView(R.id.text_title)  TextView textTitle;
        @BindView(R.id.text_score)  TextView textScore;
        @BindView(R.id.text_info)  TextView textInfo;
        //TODO: add upvote/downvote buttons

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

