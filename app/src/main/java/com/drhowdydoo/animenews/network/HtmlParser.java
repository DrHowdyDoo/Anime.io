package com.drhowdydoo.animenews.network;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;

import com.drhowdydoo.animenews.MainActivity;
import com.drhowdydoo.animenews.api.OGApi;
import com.drhowdydoo.animenews.dao.FeedDao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HtmlParser {

    private static final String TAG = "HtmlParser";
    private static List<String> oldList = new ArrayList<>();
    private final MainActivity activity;

    public HtmlParser(MainActivity activity) {
        this.activity = activity;
    }

    @SuppressLint("CheckResult")
    public void extractImageUrl(String baseUrl, List<String> pageUrls, FeedDao feedDao) {

        pageUrls.removeAll(oldList);
        if (!pageUrls.isEmpty()) {
            oldList.addAll(pageUrls);
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        OGApi api = retrofit.create(OGApi.class);
        Observable.fromIterable(pageUrls)
                .flatMap(url -> api.getPage(url)
                        .subscribeOn(Schedulers.io())
                        .map(response -> {
                            Document doc = Jsoup.parse(response);
                            Elements metaTags = doc.select("meta[property^=og:]");
                            for (Element metaTag : metaTags) {
                                String property = metaTag.attr("property");
                                if (property.equals("og:image")) {
                                    return new Pair<>(url, metaTag.attr("content"));
                                }
                            }
                            return new Pair<>(url, " ");
                        })
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(response -> {
                    Log.d(TAG, "fetchImages: guid : " + response.first + " url : " + response.second);
                    if (response.second != null) {
                        feedDao.updateFeedImage(response.second, response.first)
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }

                }, error -> {
                    Log.e(TAG, "Error fetching images !", error);
                    activity.showError("Something went wrong !");
                });

    }

}
