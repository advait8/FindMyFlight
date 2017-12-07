package com.udacity.nanodegree.advait.findmyflight.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by advaitmahashabde on 12/6/17.
 */

public class FlightTable {
    public static final String TABLE_FLIGHTS = "flights";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FLIGHT_FA_ID = "flightFaId";
    public static final String COLUMN_NUMBER = "flightNumber";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_FLIGHTS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FLIGHT_FA_ID + " text not null, "
            + COLUMN_NUMBER + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(FlightTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHTS);
        onCreate(database);
    }
}
