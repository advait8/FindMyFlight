package com.udacity.nanodegree.advait.findmyflight.service;

import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.model.AircraftInfo;
import com.udacity.nanodegree.advait.findmyflight.model.AirlineInfo;
import com.udacity.nanodegree.advait.findmyflight.model.AirportInfo;
import com.udacity.nanodegree.advait.findmyflight.model.FlightInfoStatusData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


/**
 * Created by Advait on 11/27/2017.
 */

public interface FlightAwareService {
    String FLIGHT_AWARE_URL = "https://flightxml.flightaware.com/json/FlightXML3/";
    @GET("FlightInfoStatus")
    Observable<JsonObject> getFlights(@Query("ident") String ident);

    @GET("AirlineInfo")
    Observable<AirlineInfo> getAirlineInfo(@Query("airline_code") String airlineCode);

    @GET("AirportInfo")
    Observable<AirportInfo> getAirportInfo(@Query("airport_code") String airportCode);

    @GET("AircraftType")
    Observable<JsonObject> getAircraftInfo(@Query("type") String aircraftType);

    @GET("FindFlight")
    Observable<JsonObject> findFlights(@Query("origin") String origin, @Query("destination") String destination, @Query("type") String type);


}
