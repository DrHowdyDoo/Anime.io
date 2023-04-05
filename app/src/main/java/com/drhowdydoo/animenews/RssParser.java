package com.drhowdydoo.animenews;

import android.util.Log;

import androidx.annotation.NonNull;

import com.drhowdydoo.animenews.api.RssApi;
import com.drhowdydoo.animenews.model.RssFeed;
import com.drhowdydoo.animenews.model.RssItem;
import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter;
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RssParser {

    private final MainActivity activity;

    public RssParser(MainActivity activity) {
        this.activity = activity;
    }

    private static final String TAG = "RssParser";
    private static final String BASE_URL_THUMBNAIL = "https://www.animenewsnetwork.com/cms/";

    public void getRssFeed(String baseUrl){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(TikXmlConverterFactory.create(
                        new TikXml.Builder()
                                .exceptionOnUnreadXml(false)
                                .addTypeConverter(String.class, new HtmlEscapeStringConverter())
                                .build()))
                .build();

        RssApi rssApi = retrofit.create(RssApi.class);
        Call<RssFeed> call = rssApi.getFeed();

        call.enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(@NonNull Call<RssFeed> call, @NonNull Response<RssFeed> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Response code: " + response.code());
                    Log.e(TAG, "Response Message: " + response.message());
                    Log.e(TAG, "Response Body: " + response.body());
                    return;
                }

                RssFeed feed = response.body();
                List<RssItem> items = feed.getChannel().getRssItems();

                activity.updateData(items);
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.fetchImages(BASE_URL_THUMBNAIL,items,activity.getAdapter());

            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching RSS feed", t);
            }
        });


    }

}
