package com.udacity.nanodegree.advait.findmyflight.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.instantapps.InstantApps;
import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Airline;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.model.FlightInfoStatusData;
import com.udacity.nanodegree.advait.findmyflight.persistence.MyDBHandler;
import com.udacity.nanodegree.advait.findmyflight.service.FindMyFlightService;
import com.udacity.nanodegree.advait.findmyflight.service.FlightAwareService;
import com.udacity.nanodegree.advait.findmyflight.service.ServiceFactory;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment to display flight details.
 */
public class FlightDetailsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Flight> {
    private Airline currentAirline;
    private static Flight currentFlight;

    TextView flightNumber;

    TextView aircraftType;

    TextView originAirportCode;

    TextView originCity;

    TextView destinationAirportCode;

    TextView destinationCity;

    TextView departureTime;

    TextView departureDate;

    TextView arrivalTime;

    TextView arrivalDate;

    TextView statusData;

    FloatingActionButton fabButton;

    static MyDBHandler dbHandler;

    public FlightDetailsActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new MyDBHandler(getContext(), null, null, 1);
        boolean accessedFromWidget = getActivity().getIntent().getBooleanExtra("WidgetExtra", false);
        if (accessedFromWidget) {
            getActivity().getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        } else {
            Bundle bundle = getActivity().getIntent().getBundleExtra("FlightBundle");
            currentFlight = bundle.getParcelable("Flight");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_details, container, false);
        flightNumber = view.findViewById(R.id.flightNumber);
        aircraftType = view.findViewById(R.id.aircraftType);
        originAirportCode = view.findViewById(R.id.originAirportCode);
        originCity = view.findViewById(R.id.originCity);
        destinationAirportCode = view.findViewById(R.id.destinationAirportCode);
        destinationCity = view.findViewById(R.id.destinationCity);
        departureTime = view.findViewById(R.id.departureTimeData);
        departureDate = view.findViewById(R.id.departureDateData);
        arrivalTime = view.findViewById(R.id.arrivalTimeData);
        arrivalDate = view.findViewById(R.id.arrivalDateData);
        statusData = view.findViewById(R.id.statusData);
        fabButton = view.findViewById(R.id.fabButton);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentFlight != null) {
            setupFlightView();
        }
    }

    private void setupFlightView() {
        findAirlineDetails(currentFlight.getAirline(), this.getContext());
        findAircraftDetails(currentFlight.getAircraftType(), this.getContext());
        setupCardView();
    }

    private void setupCardView() {
        originAirportCode.setText(currentFlight.getFlightOrigin().getAlternateIdent());
        originCity.setText(currentFlight.getFlightOrigin().getCity());
        destinationAirportCode.setText(currentFlight.getFlightDestination().getAlternateIdent());
        destinationCity.setText(currentFlight.getFlightDestination().getCity());
        departureTime.setText(currentFlight.getFiledDepartureTime().getTime());
        departureDate.setText(currentFlight.getFiledDepartureTime().getDate());
        arrivalTime.setText(currentFlight.getEstimatedArrivalTime().getTime());
        arrivalDate.setText(currentFlight.getEstimatedArrivalTime().getDate());
        statusData.setText(currentFlight.getStatus());
        fabButton.setOnClickListener(view -> {
            if (InstantApps.isInstantApp(getContext())) {
                redirectToGooglePlayStore();
            } else {
                storeData();
            }
        });
    }

    private void redirectToGooglePlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://play.google.com/store/apps/collection/topselling_free"));
        startActivity(intent);
    }

    private void storeData() {
        SharedPreferences preferences = getActivity().getSharedPreferences("FlightNumber", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("flightIdent", currentFlight.getFaFlightId()).commit();
        if (dbHandler != null) {
            dbHandler.addFlight(currentFlight);
        }
    }

    private void findAirlineDetails(String icaoCode, Context context) {
        FindMyFlightService findMyFlightService = ServiceFactory.createHerokuService(FindMyFlightService.class, context);
        findMyFlightService.getAirlineInfo(icaoCode).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Airline>() {
            @Override
            public void onCompleted() {
                if (currentAirline == null) {
                    currentAirline = new Airline();
                    currentAirline.setName(currentFlight.getAirline());
                } else {
                    Log.d("onCompleted", currentAirline.getName());
                }
            }

            @Override
            public void onError(Throwable e) {
                currentAirline = null;
                Log.d("onError", e.getLocalizedMessage());
            }

            @Override
            public void onNext(Airline airline) {
                currentAirline = airline;
                flightNumber.setText(currentAirline.getName() + " " + "\n(" + currentAirline.getIataCode() + currentFlight.getFlightNumber() + ")");
            }
        });
    }

    private void findAircraftDetails(String currentFlightAircraftType, Context context) {
        FlightAwareService flightAwareService = ServiceFactory.createService(FlightAwareService.class, context);
        flightAwareService.getAircraftInfo(currentFlightAircraftType).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                aircraftType.setText(" ");
                Log.d("onError", e.getLocalizedMessage());
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                aircraftType.setText(
                        jsonObject.get("AircraftTypeResult").
                                getAsJsonObject().get("manufacturer").getAsString()
                                + " " + jsonObject.get("AircraftTypeResult").
                                getAsJsonObject().get("type").getAsString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public Loader<Flight> onCreateLoader(int id, Bundle args) {
        return new FetchData(getContext(), this);
    }

    @Override
    public void onLoadFinished(Loader<Flight> loader, Flight flight) {
    }

    @Override
    public void onLoaderReset(Loader<Flight> loader) {

    }

    private static class FetchData extends AsyncTaskLoader<Flight> {
        Flight tempFlight;
        FlightDetailsActivityFragment fragment;

        public FetchData(Context context, FlightDetailsActivityFragment fragment) {
            super(context);
            this.fragment = fragment;
        }

        @Override
        public Flight loadInBackground() {
            if (dbHandler == null) {
                dbHandler = new MyDBHandler(fragment.getContext());
            }
            String flightFaId = dbHandler.getFlightFaId();
//            Log.d("OnClickWidget", flightFaId);
            FlightAwareService flightAwareService = ServiceFactory.createService(FlightAwareService.class, getContext());
            flightAwareService.getFlights(flightFaId).subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonObject>() {
                @Override
                public void onCompleted() {
                    Log.d("Loader", "Fetching data completed.");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("Loader", e.getLocalizedMessage());
                }

                @Override
                public void onNext(JsonObject jsonObject) {
                    FlightInfoStatusData flightInfoStatusData1 = new FlightInfoStatusData(jsonObject);
                    currentFlight = flightInfoStatusData1.getFlights().get(0);
                    tempFlight = currentFlight;
                    Log.d("Loader", currentFlight.getIdent());
                    fragment.setupFlightView();
                }
            });
            return tempFlight;
        }
    }
}
