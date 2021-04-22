package com.example.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dashboard.R;
import com.example.dashboard.model.feedback.Feedback;

import java.util.List;

public class RecyclerViewFeedbackAdapter extends RecyclerView.Adapter<RecyclerViewFeedbackAdapter.RecyclerViewFeedbackViewHolder> {
    private Context context;
    private List<Feedback> feedbackList;

    public RecyclerViewFeedbackAdapter(Context context, List<Feedback> feedbackList){
        this.context = context;
        this.feedbackList = feedbackList;
    }

    class RecyclerViewFeedbackViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        TextView feedbackText, feedbackRating;

        RecyclerViewFeedbackViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            feedbackText = mView.findViewById(R.id.feedback_card_text);
            feedbackRating = mView.findViewById(R.id.feedback_card_rating);
        }
    }

    @NonNull
    @Override
    public RecyclerViewFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.feedback_card, parent, false);

        return new RecyclerViewFeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewFeedbackViewHolder holder, int position) {
        switch (feedbackList.get(position).rating) {
            case 1:
                holder.feedbackRating.setText("⭐");
                break;
            case 2:
                holder.feedbackRating.setText("⭐⭐");
                break;
            case 3:
                holder.feedbackRating.setText("⭐⭐⭐");
                break;
            case 4:
                holder.feedbackRating.setText("⭐⭐⭐⭐");
                break;
            case 5:
                holder.feedbackRating.setText("⭐⭐⭐⭐⭐");
                break;
            default:
                holder.feedbackRating.setText("rating");
                break;
        }

        holder.feedbackText.setText(feedbackList.get(position).text);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
}
