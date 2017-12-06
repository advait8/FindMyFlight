package com.udacity.nanodegree.advait.findmyflight.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Advait on 12/3/2017.
 */

public class FlightContentProvider extends ContentProvider {
    private MyDBHandler myDB;
    private static final String AUTHORITY =
            "com.udacity.nanodegree.advait.findmyflight.persistence.FlightContentProvider";
    private static final String FLIGHT_TABLE = "flight";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + FLIGHT_TABLE);
    public static final int FLIGHTS = 1;
    public static final int FLIGHTS_ID = 2;
    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, FLIGHT_TABLE, FLIGHTS);
        sURIMatcher.addURI(AUTHORITY, FLIGHT_TABLE + "/#",
                FLIGHTS_ID);
    }
    @Override
    public boolean onCreate() {
        myDB = new MyDBHandler(getContext(), null, null, 1);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MyDBHandler.TABLE_FLIGHTS);

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case FLIGHTS_ID:
                queryBuilder.appendWhere(MyDBHandler.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case FLIGHTS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(myDB.getReadableDatabase(),
                strings, s, strings1, null, null, s1);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = myDB.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case FLIGHTS:
                id = sqlDB.insert(MyDBHandler.TABLE_FLIGHTS,
                        null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(FLIGHT_TABLE + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case FLIGHTS:
                rowsDeleted = sqlDB.delete(MyDBHandler.TABLE_FLIGHTS,
                        selection,
                        selectionArgs);
                break;

            case FLIGHTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MyDBHandler.TABLE_FLIGHTS,
                            MyDBHandler.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(MyDBHandler.TABLE_FLIGHTS,
                            MyDBHandler.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case FLIGHTS:
                rowsUpdated = sqlDB.update(MyDBHandler.TABLE_FLIGHTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case FLIGHTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(MyDBHandler.TABLE_FLIGHTS,
                                    values,
                                    MyDBHandler.COLUMN_ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(MyDBHandler.TABLE_FLIGHTS,
                                    values,
                                    MyDBHandler.COLUMN_ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
