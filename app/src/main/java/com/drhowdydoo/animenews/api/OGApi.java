package com.drhowdydoo.animenews.api;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface OGApi {

    @GET
    Observable<String> getPage(@Url String url);

}
