package com.drhowdydoo.animenews.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.drhowdydoo.animenews.model.RssItem;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface FeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<RssItem> feeds);

    @Query("SELECT * FROM RssItem ORDER BY pub_date DESC")
    Observable<List<RssItem>> getAllFeeds();

    @Query("UPDATE RssItem SET image_url = :imageUrl WHERE guid = :guid")
    Completable updateFeedImage(String imageUrl,String guid);

}
