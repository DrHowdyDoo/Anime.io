package com.drhowdydoo.animenews.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.drhowdydoo.animenews.R;
import com.drhowdydoo.animenews.WebPage;
import com.drhowdydoo.animenews.databinding.CardLayoutBinding;
import com.drhowdydoo.animenews.model.RssItem;

import org.jsoup.Jsoup;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {


    private final List<RssItem> feeds;
    private final Context context;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy h:mm a");

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
        holder.time.setText(rssItem.getPubDate().format(formatter));
        if (rssItem.getImageUrl() != null) {
            Glide.with(context)
                    .load(rssItem.getImageUrl())
                    .fitCenter()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .transform(new RoundedCorners(50))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .priority(Priority.HIGH)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.round_broken_image_24)
                    .into(holder.thumbnail);
        } else {
            Glide.with(context).clear(holder.thumbnail);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WebPage.class);
            intent.putExtra("com.drhowdydoo.url", rssItem.getGuid());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return feeds.size();
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
