package com.udacity.nanodegree.advait.findmyflight.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Advait on 10/22/17.
 */

public class AircraftInfo {
    private String manufacturer, type;

    public void populateData(JSONObject flightJson) {
        manufacturer = flightJson.optJSONObject("AircraftTypeResult").optString("manufacturer");
        type = flightJson.optJSONObject("AircraftTypeResult").optString("type");
    }

    public AircraftInfo(JSONObject jsonObject) {
        populateData(jsonObject);
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getType() {
        return type;
    }
}
