package com.udacity.nanodegree.advait.findmyflight.persistence;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.udacity.nanodegree.advait.findmyflight.model.Flight;

/**
 * Created by Advait on 12/3/2017.
 */

public class MyDBHandler extends SQLiteOpenHelper {
    public static final String TABLE_FLIGHTS = "flights";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FLIGHT_FA_ID = "flightFaId";
    public static final String COLUMN_NUMBER = "flightNumber";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    private static final String DATABASE_NAME = "flightTable.db";
    private static final int DATABASE_VERSION = 1;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        FlightTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        FlightTable.onUpgrade(database, oldVersion, newVersion);
    }

    public void addFlight(Flight flight) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FLIGHT_FA_ID, flight.getFaFlightId());
        values.put(COLUMN_NUMBER, flight.getIdent());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FLIGHTS, null, values);
        Log.d("FindMyFlight", "Flight record inserted");
    }

    public String getFlightFaId() {
        String query = "Select * FROM " + TABLE_FLIGHTS /*+ " WHERE " + COLUMN_NUMBER + " =  \"" + flightIdent + "\""*/;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            return cursor.getString(1);
        } else {
            return null;
        }
    }

    public boolean deleteFlight(String flightIdent) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_FLIGHTS + " WHERE " + COLUMN_NUMBER + " =  \"" + flightIdent + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            db.delete(TABLE_FLIGHTS, COLUMN_ID + " = ?",
                    new String[]{flightIdent});
            cursor.close();
            result = true;
        }

        return result;
    }
}
