package com.drhowdydoo.animenews.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.drhowdydoo.animenews.converter.ZonedDateTimeConverter;
import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.model.RssItem;

@Database(entities = {RssItem.class}, version = 1)
@TypeConverters({ZonedDateTimeConverter.class})
public abstract class FeedDatabase extends RoomDatabase {
    public abstract FeedDao feedDao();
}
