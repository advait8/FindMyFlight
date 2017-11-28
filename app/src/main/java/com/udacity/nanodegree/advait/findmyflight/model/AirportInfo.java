package com.udacity.nanodegree.advait.findmyflight.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Advait on 10/22/17.
 */

public class AirportInfo  {

    String airportName;

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public void populateData(JSONObject airportJson) {
        setAirportName(airportJson.optJSONObject("AirportInfoResult").optString("name"));
    }

    public AirportInfo(JSONObject jsonObject) {
        populateData(jsonObject);
    }
}
