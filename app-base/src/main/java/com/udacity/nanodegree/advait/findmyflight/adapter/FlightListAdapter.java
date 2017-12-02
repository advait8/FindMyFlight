package com.udacity.nanodegree.advait.findmyflight.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.util.FlightUtils;
import com.udacity.nanodegree.advait.findmyflight.view.FlightDetailsActivity;

import java.util.List;

/**
 * Created by Advait on 11/27/2017.
 */

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.ViewHolder> {
    private List<Flight> flightList;
    private Context context;
    public FlightListAdapter(List<Flight> list, Context context) {
        flightList = list;
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
        return flightList != null ? flightList.size():0;
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
            Intent intent = new Intent(context,FlightDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("Flight",flightClicked);
            intent.putExtra("FlightBundle",bundle);
            context.startActivity(intent);
        }
    }
}
