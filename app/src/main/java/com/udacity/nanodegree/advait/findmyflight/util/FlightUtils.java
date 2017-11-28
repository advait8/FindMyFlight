package com.udacity.nanodegree.advait.findmyflight.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Base64;

import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Location;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Advait on 11/27/2017.
 */

public class FlightUtils {
    public static String getAirportCode(Location location) {
        return !TextUtils.isEmpty(location.getAlternateIdent()) ? location.getAlternateIdent() : location.getAirportCode();
    }

    public static String findIcaoCode(String airlineCode, Context context) throws JSONException, IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("findMyFlight.json");
        String fileString;
        if (inputStream != null) {
            fileString = IOUtils.toString(inputStream);
            if (!TextUtils.isEmpty(fileString)) {
                JSONArray airlineJsonObject = new JSONArray(fileString);
                if (airlineJsonObject != null) {
                    for (int i = 0; i < airlineJsonObject.length(); i++) {
                        JSONObject airlineObject = airlineJsonObject.optJSONObject(i);
                        if (airlineObject.optString("iataCode").equals(airlineCode)) {
                            return airlineObject.optString("icaoCode");
                        }
                    }
                }
            }
        }
        return airlineCode;
    }

    public static String getAuthorizationHeader(Context context) {
        return "Basic " + Base64.encodeToString((context.getString(R.string.flightaware_user_name)
                        + ":" + context.getString(R.string.flightaware_api_key)).getBytes(),
                Base64.NO_WRAP);
    }
}
