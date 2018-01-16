package com.udacity.nanodegree.advait.findmyflight.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Advait on 1/15/2018.
 */

public class FlightDataContentProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.udacity.nanodegree.advait.findmyflight.provider.Flights";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/flights");
    public static final String _ID = "_id";
    public static final String FLIGHT_NUMBER = "flightNumber";
    public static final String FLIGHT_STATUS = "flightStatus";
    public static final String FLIGHT_FA_ID = "flightFaId";
    public static final int FLIGHTS = 1;


    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "flights", FLIGHTS);
    }

    SQLiteDatabase flightsDB;
    static final String DATABASE_NAME = "Flights";
    static final String DATABASE_TABLE = "flights";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE = "create table " + DATABASE_TABLE +
            " (_id integer primary key autoincrement, " + FLIGHT_NUMBER + " text not null, "
            + FLIGHT_STATUS + " text, " + FLIGHT_FA_ID + " text);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FLIGHTS:
                return "vnd.android.cursor.dir/vnd.udacity.nanodegree.advait.findmyflight" +
                        ".flights";
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = flightsDB.insert(DATABASE_TABLE, "", values);
        if (rowId > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to insert into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        flightsDB = dbHelper.getWritableDatabase();
        return flightsDB != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String
            selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(DATABASE_TABLE);

        Cursor c = sqlBuilder.query(flightsDB,projection,selection,selectionArgs,null,null,
                sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
}
