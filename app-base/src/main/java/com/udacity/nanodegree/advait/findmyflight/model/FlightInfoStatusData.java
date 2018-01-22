package com.udacity.nanodegree.advait.findmyflight.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Advait on 8/27/17.
 */

public class FlightInfoStatusData{

    int nextOffset;

    List<Flight> flights;

    public int getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void populateData(JsonObject flightData) {
        JsonObject flightInfoJsonObject = flightData.getAsJsonObject("FlightInfoStatusResult");
        if (flightInfoJsonObject != null) {
            JsonArray flightsJsonArray = flightInfoJsonObject.getAsJsonArray("flights");
            List<Flight> flightList = new ArrayList<>();
            for (int i = 0; i < flightsJsonArray.size(); i++) {
                JsonObject flightObject = flightsJsonArray.get(i).getAsJsonObject();
                Flight flight = new Flight();
                flight.populateData(flightObject);
                flightList.add(flight);
            }
            setFlights(flightList);
        }
    }

    public FlightInfoStatusData(JsonObject jsonObject) {
        populateData(jsonObject);
    }
}
