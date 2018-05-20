package com.jacquessmuts.thresher;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacquessmuts.thresher.activities.SubmissionDetailActivity;
import com.jacquessmuts.thresher.activities.SubmissionDetailFragment;
import com.jacquessmuts.thresher.activities.SubmissionListActivity;
import com.jacquessmuts.thresher.activities.dummy.DummyContent;
import com.jacquessmuts.thresher.eventbusses.SubmissionSelectedBus;
import com.squareup.picasso.Picasso;

import net.dean.jraw.models.Submission;

import java.util.List;

/**
 * Created by Jacques Smuts on 4/21/2018.
 * This is the main recyclerview, which handles the list of reddit posts, aka submissions
 */

public class SubmissionRecyclerViewAdapter
        extends RecyclerView.Adapter<SubmissionRecyclerViewAdapter.ViewHolder> {

    private final SubmissionListActivity parentActivity;
    private final List<Submission> submissions;

    public SubmissionRecyclerViewAdapter(SubmissionListActivity parent,
                                         List<Submission> items) {
        submissions = items;
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

        if (submissions == null || submissions.size() < position) return;

        Submission submission = submissions.get(position);

        if (submission.getThumbnail().isEmpty() || submission.getThumbnail().equals("default")) {
            holder.imagePreview.setVisibility(View.GONE);
        } else {
            Picasso.with(parentActivity).load(submission.getThumbnail()).into(holder.imagePreview);
            holder.imagePreview.setVisibility(View.VISIBLE);
        }

        holder.textTitle.setText(submission.getTitle());
        holder.textScore.setText(String.valueOf(submission.getScore()));

        holder.itemView.setTag(submissions.get(position));
        holder.itemView.setOnClickListener(v -> {
            SubmissionSelectedBus.getInstance().onNext(submissions.get(position));
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (submissions != null){
            count = submissions.size();
        }
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imagePreview;
        final TextView textTitle;
        final TextView textScore;
        final TextView textInfo;
        //TODO: add upvote/downvote buttons

        ViewHolder(View view) {
            super(view);
            imagePreview = view.findViewById(R.id.image_main);
            textTitle = view.findViewById(R.id.text_title);
            textScore = view.findViewById(R.id.text_score);
            textInfo = view.findViewById(R.id.text_info);
        }
    }
}

