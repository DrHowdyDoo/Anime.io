package com.drhowdydoo.animenews.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.drhowdydoo.animenews.R;
import com.drhowdydoo.animenews.databinding.CardLayoutBinding;
import com.drhowdydoo.animenews.model.RssItem;

import org.jsoup.Jsoup;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {


    private final List<RssItem> feeds;
    private final Context context;

    public RecyclerViewAdapter(List<RssItem> feeds, Context context) {
        this.feeds = feeds;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardLayoutBinding binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ItemViewHolder holder, int position) {

        RssItem rssItem = feeds.get(position);
        holder.title.setText(rssItem.getTitle());
        holder.description.setText(Jsoup.parse(rssItem.getDescription()).text());
        if (rssItem.getCategory() != null) {
            holder.category.setText(rssItem.getCategory());
        } else holder.category.setVisibility(View.GONE);
        holder.time.setText(rssItem.getPubDate());
        if (rssItem.getImageUrl() != null) {
            Glide.with(context)
                    .load(rssItem.getImageUrl())
                    .transition(withCrossFade())
                    .fitCenter()
                    .transform(new RoundedCorners(30))
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    public void updateItem(String imageUrl, int position) {
        feeds.get(position).setImageUrl(imageUrl);
        notifyItemChanged(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description, category, time;
        public ImageView thumbnail;

        public ItemViewHolder(@NonNull CardLayoutBinding binding) {
            super(binding.getRoot());
            title = binding.title;
            description = binding.description;
            category = binding.category;
            time = binding.time;
            thumbnail = binding.thumbnail;
        }
    }
}
