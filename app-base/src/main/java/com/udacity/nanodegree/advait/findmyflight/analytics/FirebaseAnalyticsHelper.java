package com.udacity.nanodegree.advait.findmyflight.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.udacity.nanodegree.advait.findmyflight.R;

/**
 * Created by Advait on 1/21/2018.
 */

public class FirebaseAnalyticsHelper {
    private static FirebaseAnalytics mFirebaseAnalytics;
    private static Context context;

    private FirebaseAnalyticsHelper() {
        //private constructor to avoid instantiation
    }

    public static FirebaseAnalytics getInstance(Context mContext) {
        context = mContext;
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }

        return mFirebaseAnalytics;
    }

    public static void setEvent(String key, Bundle bundle, Context context) {
        mFirebaseAnalytics = getInstance(context);
        mFirebaseAnalytics.logEvent(key, bundle);
    }
}
