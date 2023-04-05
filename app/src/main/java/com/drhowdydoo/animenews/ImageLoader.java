package com.drhowdydoo.animenews;

import android.util.Log;
import android.util.Pair;

import com.drhowdydoo.animenews.api.OGApi;
import com.drhowdydoo.animenews.dao.FeedDao;
import com.drhowdydoo.animenews.model.RssItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ImageLoader {

    private static final String TAG = "ImageLoader";

    public ImageLoader() {
    }

    public void fetchImages(String baseUrl, List<RssItem> items, FeedDao feedDao){


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        OGApi api = retrofit.create(OGApi.class);
        Disposable disposable = Observable.fromIterable(items)
                .flatMap(item -> api.getPage(item.getGuid())
                        .subscribeOn(Schedulers.io())
                        .map(response -> {
                            Document doc = Jsoup.parse(response);
                            Elements metaTags = doc.select("meta[property^=og:]");
                            for (Element metaTag : metaTags) {
                                String property = metaTag.attr("property");
                                if (property.equals("og:image")) {
                                    return new Pair<>(item.getGuid(), metaTag.attr("content"));
                                }
                            }
                            return new Pair<>(item.getGuid(), " ");
                        })
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(response -> {
                    // Handle response
                    Log.d(TAG, "fetchImages: guid : " + response.first + " url : " + response.second);
                    if (response.second != null) {
                        feedDao.updateFeedImage(response.second,response.first)
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }

                }, Throwable::printStackTrace);

    }

}
