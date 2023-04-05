package com.drhowdydoo.animenews.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.drhowdydoo.animenews.model.RssItem;

import java.util.List;

@Dao
public interface FeedDao {

    @Query("SELECT * FROM RssItem")
    List<RssItem> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RssItem> feeds);



}
