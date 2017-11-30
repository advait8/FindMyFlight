package com.udacity.nanodegree.advait.findmyflight.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Airline;
import com.udacity.nanodegree.advait.findmyflight.model.Location;
import com.udacity.nanodegree.advait.findmyflight.service.FindMyFlightService;
import com.udacity.nanodegree.advait.findmyflight.service.ServiceFactory;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Advait on 11/27/2017.
 */

public class FlightUtils {

    public static String getAirportCode(Location location) {
        return !TextUtils.isEmpty(location.getAlternateIdent()) ? location.getAlternateIdent() : location.getAirportCode();
    }

    public static String getAuthorizationHeader(Context context) {
        return "Basic " + Base64.encodeToString((context.getString(R.string.flightaware_user_name)
                        + ":" + context.getString(R.string.flightaware_api_key)).getBytes(),
                Base64.NO_WRAP);
    }
}
