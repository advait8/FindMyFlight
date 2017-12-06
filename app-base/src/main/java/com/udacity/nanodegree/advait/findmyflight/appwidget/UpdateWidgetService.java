package com.udacity.nanodegree.advait.findmyflight.appwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.model.FlightInfoStatusData;
import com.udacity.nanodegree.advait.findmyflight.persistence.MyDBHandler;
import com.udacity.nanodegree.advait.findmyflight.service.FlightAwareService;
import com.udacity.nanodegree.advait.findmyflight.service.ServiceFactory;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Advait on 12/4/2017.
 */

public class UpdateWidgetService extends Service {

    private static final String TAG = UpdateWidgetService.class.getSimpleName();
    MyDBHandler myDBHandler;
    String currentFlightFaId = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("widgetId")) {
            final int appWidgetId = intent.getIntExtra("widgetId", 0);
            Log.d(TAG, "onStartCommand(" + appWidgetId + ")");
            updateAppWidget(appWidgetId);
        } else {
            Log.e(TAG, "onStartCommand(<no widgetId>)");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateAppWidget(int incomingAppWidgetId) {
        myDBHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
        SharedPreferences preferences = getSharedPreferences("FlightNumber", MODE_PRIVATE);
        String flightIdent = preferences.getString("flightIdent", null);
        if (!TextUtils.isEmpty(flightIdent)) {
            currentFlightFaId = myDBHandler.getFlightFaId(flightIdent);
            if (!TextUtils.isEmpty(currentFlightFaId)) {
                findFlightDetails(currentFlightFaId, incomingAppWidgetId);
            } else {
                findFlightDetails(flightIdent,incomingAppWidgetId);
            }
        }
    }

    private void findFlightDetails(String flightFAiD, int incomingWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(getApplicationContext());

        RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.flight_widget_layout);
        FlightAwareService flightAwareService = ServiceFactory.createService(FlightAwareService.class, getApplicationContext());
        flightAwareService.getFlights(flightFAiD).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<FlightInfoStatusData>() {
            @Override
            public void onCompleted() {
                Log.d("UpdateWidgetService", "Update call completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("onError", e.getLocalizedMessage());
            }

            @Override
            public void onNext(FlightInfoStatusData flightInfoStatusData) {
                Flight updatedFlight = flightInfoStatusData.getFlights().get(0);
                remoteViews.setTextViewText(R.id.flightNumber,updatedFlight.getIdent());
                remoteViews.setTextViewText(R.id.flightStatus,updatedFlight.getStatus());
                appWidgetManager.updateAppWidget(incomingWidgetId,remoteViews);
                Log.d("UpdateWidgetService","Widget updated");
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}