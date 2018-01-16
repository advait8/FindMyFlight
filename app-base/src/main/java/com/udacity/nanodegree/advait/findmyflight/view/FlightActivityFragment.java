package com.udacity.nanodegree.advait.findmyflight.view;

import android.content.CursorLoader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.adapter.FlightListAdapter;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.persistence.FlightDataContentProvider;
import com.udacity.nanodegree.advait.findmyflight.service.FlightAwareService;
import com.udacity.nanodegree.advait.findmyflight.service.ServiceFactory;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Starting screen to look up for flights between a origin/destination pair.
 */
public class FlightActivityFragment extends Fragment {

    EditText originCityText;

    EditText destinationCityText;

    Button submitButton;

    RecyclerView recyclerView;

    ProgressBar progressBar;

    private FlightListAdapter flightListAdapter;

    public FlightActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight, container, false);
        originCityText = view.findViewById(R.id.editText);
        destinationCityText = view.findViewById(R.id.editText2);
        submitButton = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        submitButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String originAirportCode = originCityText.getText().toString();
            String destinationAirportCode = destinationCityText.getText().toString();
            if (!TextUtils.isEmpty(originAirportCode) && !TextUtils.isEmpty(destinationAirportCode)) {
                FlightAwareService service = ServiceFactory.createService(FlightAwareService.class, getContext());
                service.findFlights(originAirportCode, destinationAirportCode, "nonstop").subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<JsonObject>() {
                            @Override
                            public void onCompleted() {
                                progressBar.setVisibility(View.GONE);
                                Log.d("FlightInfoStatusData", "Completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("FlightInfoStatusData", e.getLocalizedMessage());
                            }

                            @Override
                            public void onNext(JsonObject flightInfoStatusData) {
                                try {
                                    processFlightDetails(flightInfoStatusData);
                                } catch (JSONException exception) {

                                }
                            }
                        });
            }
        });
        ArrayList<Flight> listOfPastSearchedFlights = retrievePastClickedFLights();
        flightListAdapter = new FlightListAdapter(listOfPastSearchedFlights, getContext());
        recyclerView.setAdapter(flightListAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private ArrayList<Flight> retrievePastClickedFLights() {
        ArrayList<Flight> listOfPastClickedFlights = new ArrayList<>();
        Cursor c;
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                FlightDataContentProvider.CONTENT_URI,
                null, null, null, "_id desc");
        c = cursorLoader.loadInBackground();
        if (c != null && c.moveToFirst()) {
            do {
                Flight tempFlight = new Flight();
                tempFlight.setIdent(c.getString(c.getColumnIndex(FlightDataContentProvider
                        .FLIGHT_NUMBER)));
                tempFlight.setFaFlightId(c.getString(c.getColumnIndex(FlightDataContentProvider
                        .FLIGHT_FA_ID)));
                tempFlight.setStatus(c.getString(c.getColumnIndex(FlightDataContentProvider
                        .FLIGHT_STATUS)));
                tempFlight.setRestoredFromDB(true);
                listOfPastClickedFlights.add(tempFlight);
            } while (c.moveToNext());
        }
        return listOfPastClickedFlights;
    }

    private void processFlightDetails(JsonObject flightInfoStatusData) throws JSONException {
        List<Flight> flightList = new ArrayList<>();
        JsonObject findFlightResult = flightInfoStatusData.getAsJsonObject("FindFlightResult");
        if (findFlightResult != null) {
            JsonArray flightArray = findFlightResult.getAsJsonArray("flights");
            for (int i = 0; i < flightArray.size(); i++) {
                JsonObject flightJsonObject = flightArray.get(i).getAsJsonObject();
                JsonArray segment = flightJsonObject.getAsJsonArray("segments");
                JsonObject flightSegmentJsonObject = segment.get(0).getAsJsonObject();
                Flight flightObject = new Flight();
                flightObject.populateData(flightSegmentJsonObject);
                flightList.add(flightObject);
            }
            flightListAdapter = new FlightListAdapter(flightList, getContext());
            recyclerView.setAdapter(flightListAdapter);
        } else {
            flightListAdapter = new FlightListAdapter(null, getContext());
            recyclerView.setAdapter(flightListAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
