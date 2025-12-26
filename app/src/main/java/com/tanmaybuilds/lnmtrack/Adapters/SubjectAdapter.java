package com.tanmaybuilds.lnmtrack.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanmaybuilds.lnmtrack.DataModels.SubjectModel;
import com.tanmaybuilds.lnmtrack.R;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<SubjectModel> subjectList;

    public SubjectAdapter(List<SubjectModel> subjectList) {
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_subject.xml inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectModel subject = subjectList.get(position);

        holder.courseTitle.setText(subject.getSubject());
        holder.attendedBadge.setText("Attended: " + subject.getAttended());
        holder.missedBadge.setText("Missed: " + subject.getMissedCount());

        holder.requiredBadge.setText("Req:75%");

        // Percentage handling
        int perc = subject.getPercentageInt();
        Log.d("LNM_DEBUG", "Parsed Integer for ProgressBar: " + perc);
        holder.circularProgress.setProgress(perc);
        holder.percentageText.setText(subject.getPercentage());
        Log.d("LNM_DEBUG", "Parsed Integer for ProgressBar: " + subject.getPercentage());

        holder.statusText.setText(subject.getStatus() != null ? subject.getStatus() : "On track");
        holder.lastUpdatedText.setText("Last Updated : " + subject.getLastUpdate());
    }


    @Override
    public int getItemCount() {
        return (subjectList != null) ? subjectList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, attendedBadge, missedBadge, requiredBadge, statusText, lastUpdatedText, percentageText;
        ProgressBar circularProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_subject.xml IDs
            courseTitle = itemView.findViewById(R.id.courseTitle);
            attendedBadge = itemView.findViewById(R.id.attendedBadge);
            missedBadge = itemView.findViewById(R.id.missedBadge);
            requiredBadge = itemView.findViewById(R.id.requiredBadge);
            statusText = itemView.findViewById(R.id.statusText);
            lastUpdatedText = itemView.findViewById(R.id.lastUpdatedText);
            circularProgress = itemView.findViewById(R.id.circularProgress);

            percentageText = itemView.findViewById(R.id.percentageText);

        }
    }
}
