package com.drhowdydoo.animenews.network;

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
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ImageLoader {

    private static final String TAG = "ImageLoader";

    private final MainActivity activity;

    private static List<String> oldList = new ArrayList<>();

    public ImageLoader(MainActivity activity) {
        this.activity = activity;
    }

    public void fetchImages(String baseUrl, List<String> imageUrls, FeedDao feedDao) {

        imageUrls.removeAll(oldList);
        oldList = imageUrls;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        OGApi api = retrofit.create(OGApi.class);
        Disposable disposable = Observable.fromIterable(imageUrls)
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
                    if (error instanceof retrofit2.adapter.rxjava3.HttpException) {
                        HttpException httpException = (HttpException) error;
                        try {
                            int httpCode = httpException.code();
                            if (httpCode == 429) {
                                activity.showError("Too many Requests !");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    activity.showError("Error fetching thumbnails !");
                });

    }

}
