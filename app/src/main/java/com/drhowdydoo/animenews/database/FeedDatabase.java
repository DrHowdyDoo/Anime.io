package com.drhowdydoo.animenews.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.model.RssItem;

@Database(entities = {RssItem.class}, version = 1)
public abstract class FeedDatabase extends RoomDatabase {
    public abstract FeedDao feedDao();
}
