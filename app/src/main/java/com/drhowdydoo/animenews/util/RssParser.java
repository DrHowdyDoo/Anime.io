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

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

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
            @SuppressWarnings("ResultOfMethodCallIgnored")
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
                ImageExtractor imageExtractor = new ImageExtractor(activity);

                feedDao.insertAll(items)
                        .subscribeOn(Schedulers.io())
                        .doFinally(activity::stopRefreshing)
                        .subscribe(() -> feedDao.getGuids()
                                .subscribeOn(Schedulers.io())
                                .subscribe(ids -> imageExtractor.extractImageUrl(BASE_URL_THUMBNAIL, ids, feedDao)), error -> {
                            Log.e(TAG, "Error occurred while writing to database !", error);
                            activity.showError("Something went wrong !");
                        });
            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t) {
                activity.stopRefreshing();
                if (t instanceof SocketTimeoutException) {
                    activity.showError("Network request timed out. Please Try Again");
                } else if (t instanceof UnknownHostException) {
                    activity.showError("No internet connection !");
                } else {
                    Log.e(TAG, "Error fetching RSS feed " + t);
                    activity.showError("Error fetching RSS feeds !");
                }
            }

        });


    }

}
