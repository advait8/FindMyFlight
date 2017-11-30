package com.udacity.nanodegree.advait.findmyflight.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Advait on 8/27/17.
 */

public class Flight implements Parcelable{


    String ident;


    String faFlightId;


    String airline;


    String tailNumber;

    boolean blocked;


    boolean diverted;


    boolean cancelled;

    Location flightOrigin;

    Location flightDestination;

    TimeData estimatedDepartureTime;

    TimeData actualDepartureTime;

    TimeData estimatedArrivalTime;

    TimeData actualArrivalTime;

    TimeData filedDepartureTime;

    TimeData filedArrivalTime;

    String status;

    String aircraftType;

    String flightNumber;

    public Flight(){

    }

    protected Flight(Parcel in) {
        ident = in.readString();
        faFlightId = in.readString();
        airline = in.readString();
        tailNumber = in.readString();
        blocked = in.readByte() != 0;
        diverted = in.readByte() != 0;
        cancelled = in.readByte() != 0;
        flightOrigin = in.readParcelable(Location.class.getClassLoader());
        flightDestination = in.readParcelable(Location.class.getClassLoader());
        estimatedDepartureTime = in.readParcelable(TimeData.class.getClassLoader());
        actualDepartureTime = in.readParcelable(TimeData.class.getClassLoader());
        estimatedArrivalTime = in.readParcelable(TimeData.class.getClassLoader());
        actualArrivalTime = in.readParcelable(TimeData.class.getClassLoader());
        filedDepartureTime = in.readParcelable(TimeData.class.getClassLoader());
        filedArrivalTime = in.readParcelable(TimeData.class.getClassLoader());
        status = in.readString();
        aircraftType = in.readString();
        flightNumber = in.readString();
    }

    public static final Creator<Flight> CREATOR = new Creator<Flight>() {
        @Override
        public Flight createFromParcel(Parcel in) {
            return new Flight(in);
        }

        @Override
        public Flight[] newArray(int size) {
            return new Flight[size];
        }
    };

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getFaFlightId() {
        return faFlightId;
    }

    public void setFaFlightId(String faFlightId) {
        this.faFlightId = faFlightId;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isDiverted() {
        return diverted;
    }

    public void setDiverted(boolean diverted) {
        this.diverted = diverted;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Location getFlightOrigin() {
        return flightOrigin;
    }

    public void setFlightOrigin(Location flightOrigin) {
        this.flightOrigin = flightOrigin;
    }

    public Location getFlightDestination() {
        return flightDestination;
    }

    public void setFlightDestination(Location flightDestination) {
        this.flightDestination = flightDestination;
    }

    public TimeData getEstimatedDepartureTime() {
        return estimatedDepartureTime;
    }

    public void setEstimatedDepartureTime(TimeData estimatedDepartureTime) {
        this.estimatedDepartureTime = estimatedDepartureTime;
    }

    public TimeData getActualDepartureTime() {
        return actualDepartureTime;
    }

    public void setActualDepartureTime(TimeData actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
    }

    public TimeData getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(TimeData estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public TimeData getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(TimeData actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public TimeData getFiledDepartureTime() {
        return filedDepartureTime;
    }

    public void setFiledDepartureTime(TimeData filedDepartureTime) {
        this.filedDepartureTime = filedDepartureTime;
    }

    public TimeData getFiledArrivalTime() {
        return filedArrivalTime;
    }

    public void setFiledArrivalTime(TimeData filedArrivalTime) {
        this.filedArrivalTime = filedArrivalTime;
    }

    public void populateData(JsonObject flightObject) throws JSONException {
        setIdent(flightObject.get("ident").getAsString());
        setFaFlightId(flightObject.get("faFlightID").getAsString());
        setAirline(flightObject.get("airline").getAsString());
        setBlocked(flightObject.get("blocked").getAsBoolean());
        setDiverted(flightObject.get("diverted").getAsBoolean());
        setCancelled(flightObject.get("cancelled").getAsBoolean());
        setFlightNumber(flightObject.get("flightnumber").getAsString());

        JsonObject originJsonObject = flightObject.getAsJsonObject("origin");
        Location originLocation = new Location();
        originLocation.populate(originJsonObject);
        setFlightOrigin(originLocation);

        JsonObject destinationJsonObject = flightObject.getAsJsonObject("destination");
        Location destinationLocation = new Location();
        destinationLocation.populate(destinationJsonObject);
        setFlightDestination(destinationLocation);

        JsonObject filedDepartureTimeJsonObject = flightObject.getAsJsonObject("filed_departure_time");
        TimeData filedDepartureTime = new TimeData();
        filedDepartureTime.populateData(filedDepartureTimeJsonObject);
        setFiledDepartureTime(filedDepartureTime);

        JsonObject filedArrivalTimeJsonObject = flightObject.getAsJsonObject("estimated_arrival_time");
        TimeData filedArrivalTime = new TimeData();
        filedArrivalTime.populateData(filedArrivalTimeJsonObject);
        setEstimatedArrivalTime(filedArrivalTime);

        setStatus(flightObject.get("status").getAsString());
        setAircraftType(flightObject.get("aircrafttype").getAsString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ident);
        parcel.writeString(faFlightId);
        parcel.writeString(airline);
        parcel.writeString(tailNumber);
        parcel.writeByte((byte) (blocked ? 1 : 0));
        parcel.writeByte((byte) (diverted ? 1 : 0));
        parcel.writeByte((byte) (cancelled ? 1 : 0));
        parcel.writeParcelable(flightOrigin, i);
        parcel.writeParcelable(flightDestination, i);
        parcel.writeParcelable(estimatedDepartureTime, i);
        parcel.writeParcelable(actualDepartureTime, i);
        parcel.writeParcelable(estimatedArrivalTime, i);
        parcel.writeParcelable(actualArrivalTime, i);
        parcel.writeParcelable(filedDepartureTime, i);
        parcel.writeParcelable(filedArrivalTime, i);
        parcel.writeString(status);
        parcel.writeString(aircraftType);
        parcel.writeString(flightNumber);
    }
}
