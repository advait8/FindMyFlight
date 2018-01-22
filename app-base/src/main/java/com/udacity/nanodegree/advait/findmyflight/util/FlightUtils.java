package com.udacity.nanodegree.advait.findmyflight.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Location;


/**
 * Created by Advait on 11/27/2017.
 */

public final class FlightUtils {

    public static String getAirportCode(Location location) {
        return !TextUtils.isEmpty(location.getAlternateIdent()) ? location.getAlternateIdent() : location.getAirportCode();
    }

    public static String getAuthorizationHeader(Context context) {
        return "Basic " + Base64.encodeToString((context.getString(R.string.flightaware_user_name)
                        + ":" + context.getString(R.string.flightaware_api_key)).getBytes(),
                Base64.NO_WRAP);
    }
}
