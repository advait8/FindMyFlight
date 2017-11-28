package com.udacity.nanodegree.advait.findmyflight.model;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public void populateData(JSONObject flightData) {
        JSONObject flightInfoJsonObject = flightData.optJSONObject("FlightInfoStatusResult");
        if (flightInfoJsonObject != null) {
            JSONArray flightsJsonArray = flightInfoJsonObject.optJSONArray("flights");
            List<Flight> flightList = new ArrayList<>();
            for (int i = 0; i < flightsJsonArray.length(); i++) {
                JSONObject flightObject = flightsJsonArray.optJSONObject(i);
                //Flight flight = new Flight();
                //flight.populateData(new JsonObject(flightObject.toString()));
                //flightList.add(flight);
            }
            setFlights(flightList);
        }
    }

    public FlightInfoStatusData(JSONObject jsonObject) {
        populateData(jsonObject);
    }
}
