package com.drhowdydoo.animenews.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.drhowdydoo.animenews.util.ZonedDateTimeConverter;
import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.model.RssItem;

@Database(entities = {RssItem.class}, version = 1)
@TypeConverters({ZonedDateTimeConverter.class})
public abstract class FeedDatabase extends RoomDatabase {

    private static FeedDatabase INSTANCE;

    public static synchronized FeedDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FeedDatabase.class, "drhowdydoo-feedDb")
                    .build();
        }
        return INSTANCE;
    }

    public abstract FeedDao feedDao();
}
