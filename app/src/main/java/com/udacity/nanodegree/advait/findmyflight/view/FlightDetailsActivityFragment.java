package com.udacity.nanodegree.advait.findmyflight.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.udacity.nanodegree.advait.findmyflight.R;
import com.udacity.nanodegree.advait.findmyflight.model.Airline;
import com.udacity.nanodegree.advait.findmyflight.model.Flight;
import com.udacity.nanodegree.advait.findmyflight.service.FindMyFlightService;
import com.udacity.nanodegree.advait.findmyflight.service.FlightAwareService;
import com.udacity.nanodegree.advait.findmyflight.service.ServiceFactory;

import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A fragment to display flight details.
 */
public class FlightDetailsActivityFragment extends Fragment {
    private Airline currentAirline;
    private Flight currentFlight;
    private Unbinder unbinder;

    @BindView(R.id.flightNumber)
    TextView flightNumber;

    @BindView(R.id.aircraftType)
    TextView aircraftType;

    @BindView(R.id.originAirportCode)
    TextView originAirportCode;

    @BindView(R.id.originCity)
    TextView originCity;

    @BindView(R.id.destinationAirportCode)
    TextView destinationAirportCode;

    @BindView(R.id.destinationCity)
    TextView destinationCity;

    @BindView(R.id.departureTimeData)
    TextView departureTime;

    @BindView(R.id.departureDateData)
    TextView departureDate;

    @BindView(R.id.arrivalTimeData)
    TextView arrivalTime;

    @BindView(R.id.arrivalDateData)
    TextView arrivalDate;

    @BindView(R.id.statusData)
    TextView statusData;

    public FlightDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getActivity().getIntent().getBundleExtra("FlightBundle");
        currentFlight = bundle.getParcelable("Flight");
        View view = inflater.inflate(R.layout.fragment_flight_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentFlight != null) {
            findAirlineDetails(currentFlight.getAirline(), this.getContext());
            findAircraftDetails(currentFlight.getAircraftType(), this.getContext());
            setupCardView();
        }

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
        unbinder.unbind();
    }
}
