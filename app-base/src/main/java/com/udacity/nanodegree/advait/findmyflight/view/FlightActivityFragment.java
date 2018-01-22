package com.udacity.nanodegree.advait.findmyflight.view;

import android.content.CursorLoader;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.adapter.FlightListAdapter;
import com.udacity.nanodegree.advait.findmyflight.analytics.FirebaseAnalyticsHelper;
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

    TextView title;

    private FlightListAdapter flightListAdapter;

    ImageView clearButton;

    public FlightActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight, container, false);
        originCityText = view.findViewById(R.id.originAirportEditText);
        destinationCityText = view.findViewById(R.id.destinationAirportEditText);
        submitButton = view.findViewById(R.id.submitButton);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        title = view.findViewById(R.id.recyclerViewTitle);
        clearButton = view.findViewById(R.id.imageView2);
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
                            }

                            @Override
                            public void onError(Throwable e) {
                                originCityText.setText("");
                                destinationCityText.setText("");
                                Snackbar snackbar = Snackbar.make(FlightActivityFragment.this
                                                .getView(), getString(R.string.api_error_generic_response),
                                        Snackbar.LENGTH_LONG);
                                snackbar.show();
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
            Bundle eventBundle = new Bundle();
            eventBundle.putString("FindMyFlight", getString(R.string.fbase_event_search_flights));
            FirebaseAnalyticsHelper.setEvent(FirebaseAnalytics.Event.SEARCH, eventBundle, getContext());
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().getContentResolver().delete(FlightDataContentProvider.CONTENT_URI,
                        null, null);
                ArrayList<Flight> listOfPastSearchedFlights = retrievePastClickedFLights();
                flightListAdapter = new FlightListAdapter(listOfPastSearchedFlights, getContext(), recyclerView);
                recyclerView.setAdapter(flightListAdapter);
                Bundle eventBundle = new Bundle();
                eventBundle.putString("FindMyFlight", getString(R.string.fbase_event_clear_flight_list));
                FirebaseAnalyticsHelper.setEvent(FirebaseAnalytics.Event.SEARCH, eventBundle, getContext());
            }
        });
        ArrayList<Flight> listOfPastSearchedFlights = retrievePastClickedFLights();
        title.setText(getString(R.string.recently_searched_flights));
        flightListAdapter = new FlightListAdapter(listOfPastSearchedFlights, getContext(), recyclerView);
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
        title.setText(getString(R.string.search_results));
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
            flightListAdapter = new FlightListAdapter(flightList, getContext(), recyclerView);
            recyclerView.setAdapter(flightListAdapter);
        } else {
            flightListAdapter = new FlightListAdapter(null, getContext(), recyclerView);
            recyclerView.setAdapter(flightListAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
