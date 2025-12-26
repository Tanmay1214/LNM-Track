package com.tanmaybuilds.lnmtrack.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tanmaybuilds.lnmtrack.DataModels.WelcomeItem;
import com.tanmaybuilds.lnmtrack.R;

import java.util.List;

public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.WelcomeViewHolder> {
    private List<WelcomeItem> WelcomeItems;


    public WelcomeAdapter(List<WelcomeItem> WelcomeItems) {
        this.WelcomeItems = WelcomeItems;
    }

    @Override
    public WelcomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WelcomeViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(WelcomeViewHolder holder, int position) {
        holder.setWelcomeData(WelcomeItems.get(position));
    }

    @Override
    public int getItemCount() { return WelcomeItems.size(); }

    class WelcomeViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title, description;

        WelcomeViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.imgWelcome);
            title = view.findViewById(R.id.textTitle);
            description = view.findViewById(R.id.textDescription);
        }

        void setWelcomeData(WelcomeItem item) {
            image.setImageResource(item.getImage());
            title.setText(item.getTitle());
            description.setText(item.getDescription());
        }
    }
}
