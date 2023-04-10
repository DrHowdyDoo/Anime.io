package com.drhowdydoo.animenews.util;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.drhowdydoo.animenews.MainActivity;
import com.drhowdydoo.animenews.api.RssApi;
import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.model.RssFeed;
import com.drhowdydoo.animenews.model.RssItem;
import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter;
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RssParser {

    private static final String TAG = "RssParser";
    private static final String BASE_URL_THUMBNAIL = "https://www.animenewsnetwork.com/cms/";
    private final MainActivity activity;
    private final FeedDao feedDao;

    public RssParser(MainActivity activity, FeedDao feedDao) {
        this.activity = activity;
        this.feedDao = feedDao;
    }

    public void getRssFeed(String baseUrl) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(TikXmlConverterFactory.create(
                        new TikXml.Builder()
                                .exceptionOnUnreadXml(false)
                                .addTypeConverter(String.class, new HtmlEscapeStringConverter())
                                .build()))
                .build();

        RssApi rssApi = retrofit.create(RssApi.class);
        Call<RssFeed> callRssApi = rssApi.getFeed();

        callRssApi.enqueue(new Callback<RssFeed>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(@NonNull Call<RssFeed> call, @NonNull Response<RssFeed> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Response Code: " + response.code());
                    Log.e(TAG, "Response Message: " + response.message());
                    return;
                }

                RssFeed feed = response.body();
                if (feed == null) {
                    activity.stopRefreshing();
                    activity.showError("Received empty response from the server !");
                    return;
                }
                List<RssItem> items = feed.getChannel().getRssItems();

                feedDao.insertAll(items)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(activity::stopRefreshing)
                        .subscribe(() -> {

                        }, error -> {
                            Log.e(TAG, "Error occurred while writing to database !", error);
                            activity.showError("Something went wrong !");
                        });

                ImageExtractor imageExtractor = new ImageExtractor(activity);
                List<String> pageUrls = items.stream().map(RssItem::getGuid).collect(Collectors.toList());
                if (!pageUrls.isEmpty()) {
                    imageExtractor.extractImageUrl(BASE_URL_THUMBNAIL, pageUrls, feedDao);
                }

            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching RSS feed", t);
                activity.stopRefreshing();
                activity.showError("Error fetching RSS feeds !");
            }

        });


    }

}
