package com.drhowdydoo.animenews.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.drhowdydoo.animenews.model.RssItem;

import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FeedDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAll(List<RssItem> feeds);

    @Query("SELECT * FROM RssItem ORDER BY datetime(pub_date) DESC")
    Observable<List<RssItem>> getAllFeeds();

    @Query("SELECT * FROM RssItem WHERE category IN (:category) ORDER BY datetime(pub_date) DESC")
    Observable<List<RssItem>> getAllFeeds(Set<String> category);

    @Query("UPDATE RssItem SET image_url = :imageUrl WHERE guid = :guid AND image_url IS NULL")
    Completable updateFeedImage(String imageUrl, String guid);

    @Query("SELECT COUNT(*) FROM RssItem")
    Single<Long> getRowCount();

    @Query("DELETE FROM RssItem WHERE guid NOT IN (SELECT guid FROM RssItem ORDER BY pub_date DESC LIMIT 50)")
    void deleteOldestRows();

    @Query("SELECT guid FROM RssItem WHERE image_url IS NULL")
    Maybe<List<String>> getGuids();

}
