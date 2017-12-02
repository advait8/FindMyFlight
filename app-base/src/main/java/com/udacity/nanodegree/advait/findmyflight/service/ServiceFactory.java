package com.udacity.nanodegree.advait.findmyflight.service;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.udacity.nanodegree.advait.findmyflight.util.FlightUtils;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Advait on 11/27/2017.
 */

public class ServiceFactory {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder;

    private static Retrofit retrofit;

    public static <S> S createService(
            Class<S> serviceClass, Context context) {
        return createService(serviceClass, FlightUtils.getAuthorizationHeader(context));
    }

    public static <S> S createService(
            Class<S> serviceClass, final String authToken) {
        builder = new Retrofit.Builder()
                .baseUrl(FlightAwareService.FLIGHT_AWARE_URL)
                .addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                builder.client(httpClient.build());

            }
        }
        retrofit = builder.build();
        return retrofit.create(serviceClass);

    }

    public static <S> S createHerokuService(Class<S> serviceClass, Context context) {
        builder = new Retrofit.Builder()
                .baseUrl(FindMyFlightService.FINDMYFLIGHT_HEROKU_URL)
                .addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        builder.client(httpClient.build());
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
