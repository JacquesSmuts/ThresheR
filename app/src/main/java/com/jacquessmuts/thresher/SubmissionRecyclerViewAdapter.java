package com.jacquessmuts.thresher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacquessmuts.thresher.activities.SubmissionDetailActivity;
import com.jacquessmuts.thresher.activities.SubmissionDetailFragment;
import com.jacquessmuts.thresher.activities.SubmissionListActivity;
import com.jacquessmuts.thresher.activities.dummy.DummyContent;

import net.dean.jraw.models.Submission;

import java.util.List;

/**
 * Created by Jacques Smuts on 4/21/2018.
 * TODO: set header description
 */

public class SubmissionRecyclerViewAdapter
        extends RecyclerView.Adapter<SubmissionRecyclerViewAdapter.ViewHolder> {

    private final SubmissionListActivity mParentActivity;
    private final List<Submission> mValues;

    private final View.OnClickListener mOnClickListener = view -> {
        DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
        Context context = view.getContext();
        Intent intent = new Intent(context, SubmissionDetailActivity.class);
        intent.putExtra(SubmissionDetailFragment.ARG_ITEM_ID, item.id);
        context.startActivity(intent);
    };

    public SubmissionRecyclerViewAdapter(SubmissionListActivity parent,
                                         List<Submission> items) {
        mValues = items;
        mParentActivity = parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.submission_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mIdView.setText(mValues.get(position).getTitle());
//        holder.mContentView.setText(mValues.get(position).getSelfText());

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mValues != null){
            count = mValues.size();
        }
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
//        final TextView mIdView;
//        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
//            mIdView = (TextView) view.findViewById(R.id.id_text);
//            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }
}

