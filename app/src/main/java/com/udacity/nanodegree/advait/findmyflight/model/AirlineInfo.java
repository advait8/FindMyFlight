package com.udacity.nanodegree.advait.findmyflight.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Advait on 10/22/17.
 */

public class AirlineInfo {

    String name;

    String shortName;

    String callSign;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public void populateData(JSONObject jsonObject) {
        setName(jsonObject.optJSONObject("AirlineInfoResult").optString("name"));
        setShortName(jsonObject.optJSONObject("AirlineInfoResult").optString("shortname"));
        setCallSign(jsonObject.optJSONObject("AirlineInfoResult").optString("callsign"));
    }

    public AirlineInfo(JSONObject jsonObject) {
        populateData(jsonObject);
    }
}
