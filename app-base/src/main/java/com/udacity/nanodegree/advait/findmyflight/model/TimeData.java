package com.udacity.nanodegree.advait.findmyflight.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by Advait on 8/27/17.
 */

public class TimeData implements Parcelable {


    private long epoch;


    private String timeZone;


    private String dayOfTheWeek;


    private String time;


    private String date;


    private long localTime;

    protected TimeData(Parcel in) {
        epoch = in.readLong();
        timeZone = in.readString();
        dayOfTheWeek = in.readString();
        time = in.readString();
        date = in.readString();
        localTime = in.readLong();
    }

    public static final Creator<TimeData> CREATOR = new Creator<TimeData>() {
        @Override
        public TimeData createFromParcel(Parcel in) {
            return new TimeData(in);
        }

        @Override
        public TimeData[] newArray(int size) {
            return new TimeData[size];
        }
    };

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getLocalTime() {
        return localTime;
    }

    public void setLocalTime(long localTime) {
        this.localTime = localTime;
    }

    public void populateData(JsonObject timeDataJsonObject) {
        setEpoch(timeDataJsonObject.get("epoch").getAsLong());
        setTimeZone(timeDataJsonObject.get("tz").getAsString());
        setDayOfTheWeek(timeDataJsonObject.get("dow").getAsString());
        setTime(timeDataJsonObject.get("time").getAsString());
        setDate(timeDataJsonObject.get("date").getAsString());
        setLocalTime(timeDataJsonObject.get("localtime").getAsLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(epoch);
        dest.writeString(timeZone);
        dest.writeString(dayOfTheWeek);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeLong(localTime);
    }

    public TimeData() {
    }
}
