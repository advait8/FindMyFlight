package com.udacity.nanodegree.advait.findmyflight.view;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.instantapps.InstantApps;
import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.analytics.FirebaseAnalyticsHelper;
import com.udacity.nanodegree.advait.findmyflight.appwidget.FlightWidgetProvider;
import com.udacity.nanodegree.advait.findmyflight.model.Airline;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.model.FlightInfoStatusData;
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

    boolean isBeingTracked;

    private AdView mAdView;

    private LinearLayout originData, destinationData;

    public FlightDetailsActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mAdView = view.findViewById(R.id.adView);
        originData = view.findViewById(R.id.originComponent);
        destinationData = view.findViewById(R.id.destinationData);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getContext().getSharedPreferences("FlightNumber", MODE_PRIVATE);
        String flightIdent = preferences.getString("flightIdent", null);
        if (!TextUtils.isEmpty(flightIdent) && flightIdent.equalsIgnoreCase(currentFlight.getFaFlightId())) {
            isBeingTracked = true;
        }
        if (currentFlight != null) {
            setupFlightView();
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupFlightView() {
        findAirlineDetails(currentFlight.getAirline(), this.getContext());
        findAircraftDetails(currentFlight.getAircraftType(), this.getContext());
        setupCardView();
    }

    private void setupCardView() {
        if (!TextUtils.isEmpty(currentFlight.getFlightOrigin().getAlternateIdent())) {
            originAirportCode.setText(currentFlight.getFlightOrigin().getAlternateIdent());
        } else {
            originAirportCode.setText(currentFlight.getFlightOrigin().getAirportCode());
        }
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
            } else if (isBeingTracked) {
                deleteData();
                fabButton.setContentDescription(String.format(getString(R.string.cd_stop_track_flight_button)));
            } else {
                storeData();
                fabButton.setContentDescription(String.format(getString(R.string.cd_start_track_flight_button)));
            }
        });

        originData.setContentDescription(String.format(getString(R.string.cd_origin_airport),
                currentFlight.getFlightOrigin().getCity(), currentFlight.getFiledDepartureTime()
                        .getTime() + "on" + currentFlight.getFiledDepartureTime().getDate()));
        destinationData.setContentDescription(String.format(getString(R.string.cd_destination_airport),
                currentFlight.getFlightDestination().getCity(), currentFlight.getEstimatedArrivalTime()
                        .getTime() + " on " + currentFlight.getEstimatedArrivalTime().getDate()));
        statusData.setContentDescription(String.format(getString(R.string.cd_flight_status),
                currentFlight.getStatus()));

    }

    private void redirectToGooglePlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://play.google.com/store/apps/collection/topselling_free"));
        startActivity(intent);
    }

    private void storeData() {
        Bundle eventBundle = new Bundle();
        SharedPreferences preferences = getActivity().getSharedPreferences("FlightNumber", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("flightIdent", currentFlight.getFaFlightId()).apply();
        isBeingTracked = true;
        fabButton.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_delete));
        eventBundle.putString("FindMyFlight", getString(R.string.fbase_event_start_flight_track));
        FirebaseAnalyticsHelper.setEvent("FlightDetails", eventBundle, getContext());
        updateAppWidget();
    }

    private void deleteData() {
        Bundle eventBundle = new Bundle();
        SharedPreferences preferences = getActivity().getSharedPreferences("FlightNumber", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("flightIdent", null).apply();
        isBeingTracked = false;
        fabButton.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_input_add));
        eventBundle.putString("FindMyFlight", getString(R.string.fbase_event_stop_tracking_flight));
        FirebaseAnalyticsHelper.setEvent("FlightDetails", eventBundle, getContext());
        updateAppWidget();
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
                flightNumber.setText(String.format(getString(R.string
                        .airline_name_and_flight_number), currentFlight.getAirline(), currentFlight
                        .getIdent()));
                flightNumber.setContentDescription(String.format(getString(R.string.cd_flight_number),
                        currentFlight.getIdent(), currentFlight.getAirline()));
                Log.d("onError", e.getLocalizedMessage());
            }

            @Override
            public void onNext(Airline airline) {
                currentAirline = airline;
                flightNumber.setText(String.format(getString(R.string
                        .airline_name_and_flight_number), currentAirline.getName(), currentAirline
                        .getIataCode() +
                        currentFlight.getFlightNumber()));
                flightNumber.setContentDescription(String.format(getString(R.string.cd_flight_number),
                        currentAirline.getIataCode() + currentFlight.getFlightNumber(),
                        currentAirline.getName()));
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
                aircraftType.setText(currentFlight.getAircraftType());
                aircraftType.setContentDescription(String.format(getString(R.string
                        .cd_aircraft_type), currentFlight.getAircraftType()));
                Log.d("onError", e.getLocalizedMessage());
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                String manufacturer = jsonObject.get("AircraftTypeResult").
                        getAsJsonObject().get("manufacturer").getAsString();
                String type = jsonObject.get("AircraftTypeResult").
                        getAsJsonObject().get("type").getAsString();
                aircraftType.setText(String.format(getString(R.string
                        .aircraft_manufacturer_and_model), manufacturer, type));
                aircraftType.setContentDescription(String.format(getString(R.string
                        .cd_aircraft_type), manufacturer + " " + type));
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

        private FetchData(Context context, FlightDetailsActivityFragment fragment) {
            super(context);
            this.fragment = fragment;
        }

        @Override
        public Flight loadInBackground() {
            SharedPreferences preferences = getContext().getSharedPreferences("FlightNumber",
                    MODE_PRIVATE);

            String flightIdent = preferences.getString("flightIdent", null);
//            Log.d("OnClickWidget", flightFaId);
            FlightAwareService flightAwareService = ServiceFactory.createService(FlightAwareService.class, getContext());
            flightAwareService.getFlights(flightIdent).subscribeOn(Schedulers.newThread()).
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

    private void updateAppWidget() {
        Intent intent = new Intent(getContext(), FlightWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new
                ComponentName(getContext(), FlightWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getContext().sendBroadcast(intent);
    }
}
