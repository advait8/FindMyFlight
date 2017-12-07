package com.udacity.nanodegree.advait.findmyflight.persistence;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.udacity.nanodegree.advait.findmyflight.model.Flight;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Advait on 12/3/2017.
 */

public class FlightContentProvider extends ContentProvider {
    // database
    private MyDBHandler database;

    // used for the UriMacher
    private static final int FLIGHTS = 10;
    private static final int FLIGHT_ID = 20;

    private static final String AUTHORITY = "com.udacity.nanodegree.advait.findmyflight";

    private static final String BASE_PATH = "findmyflight";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/findmyflight";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/findmyflight";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, FLIGHTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", FLIGHT_ID);
    }

    @Override
    public boolean onCreate() {
        database = new MyDBHandler(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(MyDBHandler.TABLE_FLIGHTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FLIGHTS:
                break;
            case FLIGHT_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(FlightTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case FLIGHTS:
                id = sqlDB.insert(FlightTable.TABLE_FLIGHTS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case FLIGHTS:
                rowsDeleted = sqlDB.delete(FlightTable.TABLE_FLIGHTS, selection,
                        selectionArgs);
                break;
            case FLIGHT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            FlightTable.TABLE_FLIGHTS,
                            FlightTable.COLUMN_NUMBER + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            FlightTable.TABLE_FLIGHTS,
                            FlightTable.COLUMN_NUMBER + "=" + id
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
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case FLIGHTS:
                rowsUpdated = sqlDB.update(FlightTable.TABLE_FLIGHTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case FLIGHT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(FlightTable.TABLE_FLIGHTS,
                            values,
                            FlightTable.COLUMN_NUMBER + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(FlightTable.TABLE_FLIGHTS,
                            values,
                            FlightTable.COLUMN_NUMBER + "=" + id
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

    private void checkColumns(String[] projection) {
        String[] available = {FlightTable.COLUMN_ID, FlightTable.COLUMN_FLIGHT_FA_ID, FlightTable.COLUMN_ID};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }

}

