package com.drhowdydoo.animenews.api;

import com.drhowdydoo.animenews.model.RssFeed;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RssApi {
    @GET("rss.xml")
    Call<RssFeed> getFeed();
}

