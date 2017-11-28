package com.udacity.nanodegree.advait.findmyflight.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.util.FlightUtils;

import java.util.List;

/**
 * Created by Advait on 11/27/2017.
 */

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.ViewHolder> {
    private List<Flight> flightList;

    public FlightListAdapter(List<Flight> list) {
        flightList = list;
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
        return flightList != null ? flightList.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView airlineName;
        TextView flightNumber;
        TextView status;

        ViewHolder(View itemView) {
            super(itemView);
            airlineName = itemView.findViewById(R.id.airlineTextView);
            flightNumber = itemView.findViewById(R.id.flightNumberTextView);
            status = itemView.findViewById(R.id.flightStatus);
        }
    }
}
