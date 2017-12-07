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
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "flightDB.db";
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FLIGHTS_TABLE = "CREATE TABLE " +
                TABLE_FLIGHTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_FLIGHT_FA_ID
                + " TEXT," + COLUMN_NUMBER + " TEXT" + ")";
        db.execSQL(CREATE_FLIGHTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHTS);
        onCreate(sqLiteDatabase);
    }

    public void addFlight(Flight flight){
        ContentValues values = new ContentValues();
        values.put(COLUMN_FLIGHT_FA_ID, flight.getFaFlightId());
        values.put(COLUMN_NUMBER, flight.getIdent());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FLIGHTS,null, values);
        Log.d("FindMyFlight","Flight record inserted");
    }

    public String getFlightFaId(){
        String query = "Select * FROM " + TABLE_FLIGHTS /*+ " WHERE " + COLUMN_NUMBER + " =  \"" + flightIdent + "\""*/;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
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
                    new String[] { flightIdent });
            cursor.close();
            result = true;
        }

        return result;
    }
}
