package com.udacity.nanodegree.advait.findmyflight.model;



import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by Advait on 8/27/17.
 */

public class Location implements Parcelable{

    String city;


    String airportCode;


    String alternateIdent;


    String airportName;

    protected Location(Parcel in) {
        city = in.readString();
        airportCode = in.readString();
        alternateIdent = in.readString();
        airportName = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAlternateIdent() {
        return alternateIdent;
    }

    public void setAlternateIdent(String alternateIdent) {
        this.alternateIdent = alternateIdent;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public void populate(JsonObject locationJsonObject) {
        setCity(locationJsonObject.get("city").getAsString());
        setAirportCode(locationJsonObject.get("code").getAsString());
        setAlternateIdent(locationJsonObject.get("alternate_ident").getAsString());
        setAirportName(locationJsonObject.get("airport_name").getAsString());
    }

    public Location() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(city);
        parcel.writeString(airportCode);
        parcel.writeString(alternateIdent);
        parcel.writeString(airportName);
    }
}
