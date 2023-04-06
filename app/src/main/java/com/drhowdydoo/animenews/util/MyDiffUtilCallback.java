package com.drhowdydoo.animenews.util;

import androidx.recyclerview.widget.DiffUtil;

import com.drhowdydoo.animenews.model.RssItem;

import java.util.List;

public class MyDiffUtilCallback extends DiffUtil.Callback {
    private List<RssItem> oldList;
    private List<RssItem> newList;

    public MyDiffUtilCallback(List<RssItem> oldList, List<RssItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        RssItem oldItem = oldList.get(oldItemPosition);
        RssItem newItem = newList.get(newItemPosition);
        return oldItem.getGuid().equalsIgnoreCase(newItem.getGuid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        RssItem oldItem = oldList.get(oldItemPosition);
        RssItem newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}

