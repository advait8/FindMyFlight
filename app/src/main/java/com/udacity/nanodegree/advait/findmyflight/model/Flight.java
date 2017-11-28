package com.udacity.nanodegree.advait.findmyflight.model;


import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Advait on 8/27/17.
 */

public class Flight {


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

        setStatus(flightObject.get("status").getAsString());
        setAircraftType(flightObject.get("aircrafttype").getAsString());
    }
}
