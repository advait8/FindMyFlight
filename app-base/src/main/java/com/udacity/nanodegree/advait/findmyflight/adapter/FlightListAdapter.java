package com.udacity.nanodegree.advait.findmyflight.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.model.FlightInfoStatusData;
import com.udacity.nanodegree.advait.findmyflight.persistence.FlightDataContentProvider;
import com.udacity.nanodegree.advait.findmyflight.service.FlightAwareService;
import com.udacity.nanodegree.advait.findmyflight.service.ServiceFactory;
import com.udacity.nanodegree.advait.findmyflight.view.FlightDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Advait on 11/27/2017.
 */

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.ViewHolder> {
    private List<Flight> flightList;
    private Context context;

    public FlightListAdapter(List<Flight> list, Context context) {
        if (list == null) {
            flightList = new ArrayList<>();
            Flight noFlight = new Flight();
            noFlight.setAirline(context.getString(R.string.no_flights_found));
        } else {
            flightList = list;
        }
        this.context = context;
    }

    @Override
    public FlightListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FlightListAdapter.ViewHolder holder, int position) {
        Flight flight = flightList.get(position);
        holder.airlineName.setText(flight.getAirline());
        holder.flightNumber.setText(flight.getIdent());
        holder.status.setText(flight.getStatus());
    }

    @Override
    public int getItemCount() {
        return flightList != null ? flightList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView airlineName;
        TextView flightNumber;
        TextView status;

        ViewHolder(View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.airlineTextView);
            flightNumber = itemView.findViewById(R.id.flightNumberTextView);
            status = itemView.findViewById(R.id.flightStatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Flight flightClicked = flightList.get(getLayoutPosition());
            if (flightClicked.isRestoredFromDB()) {
                FlightAwareService flightAwareService = ServiceFactory.createService
                        (FlightAwareService.class, context);
                flightAwareService.getFlights(flightClicked.getIdent()).subscribeOn(Schedulers
                        .newThread()).
                        observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d("UpdateWidgetService", "Update call completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onError", e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(JsonObject flightInfoStatusDataJsonObject) {
                        FlightInfoStatusData flightInfoStatusData = new FlightInfoStatusData
                                (flightInfoStatusDataJsonObject);
                        Flight updatedFlight = flightInfoStatusData.getFlights().get(0);
                        Intent intent = new Intent(context, FlightDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("Flight", updatedFlight);
                        intent.putExtra("FlightBundle", bundle);
                        context.startActivity(intent);
                        Log.d("UpdateWidgetService", "Widget updated");
                    }
                });
            } else {
                addFlightToDb(flightClicked);
                Intent intent = new Intent(context, FlightDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Flight", flightClicked);
                intent.putExtra("FlightBundle", bundle);
                context.startActivity(intent);
            }

        }
    }

    private void addFlightToDb(Flight flightClicked) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FlightDataContentProvider.FLIGHT_NUMBER, flightClicked.getIdent());
        contentValues.put(FlightDataContentProvider.FLIGHT_FA_ID, flightClicked.getFaFlightId());
        contentValues.put(FlightDataContentProvider.FLIGHT_STATUS, flightClicked.getStatus());

        Uri uri = context.getContentResolver().insert(FlightDataContentProvider.CONTENT_URI,
                contentValues);
        Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show();
    }
}
