package com.udacity.nanodegree.advait.findmyflight.service;

import com.udacity.nanodegree.advait.findmyflight.model.Airline;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Advait on 11/29/2017.
 */

public interface FindMyFlightService {
    public static final String FINDMYFLIGHT_HEROKU_URL = "https://findmyflight123.herokuapp.com/";

    @GET("iataCode")
    Observable<Airline> getAirlineInfo(@Query("icaoCode")String icaoCode);

}
