package com.udacity.nanodegree.advait.findmyflight.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Advait on 12/4/2017.
 */

public class FlightWidgetProvider extends AppWidgetProvider  {

        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
        }

        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            super.onUpdate(context, appWidgetManager, appWidgetIds);

            for (int appWidgetId : appWidgetIds) {
                Intent intentService = new Intent(context, UpdateWidgetService.class);
                intentService.setAction("refresh");
                intentService.putExtra("widgetId", appWidgetId);
                context.startService(intentService);
            }
        }

}
