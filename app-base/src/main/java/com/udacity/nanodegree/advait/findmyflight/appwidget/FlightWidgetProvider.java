package com.udacity.nanodegree.advait.findmyflight.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.view.FlightDetailsActivity;

/**
 * Created by Advait on 12/4/2017.
 */

public class FlightWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.flight_widget_layout);
        for (int appWidgetId : appWidgetIds) {
            Intent intentService = new Intent(context, UpdateWidgetService.class);
            intentService.setAction("refresh");
            intentService.putExtra("widgetId", appWidgetId);
            context.startService(intentService);

            Intent intent = new Intent(context, FlightDetailsActivity.class);
            intent.putExtra("WidgetExtra", true);

            PendingIntent myPI = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.flightWidget, myPI);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }
    }

}
