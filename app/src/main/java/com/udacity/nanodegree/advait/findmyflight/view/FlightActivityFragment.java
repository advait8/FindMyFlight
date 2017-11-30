package com.udacity.nanodegree.advait.findmyflight.view;

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
import com.udacity.nanodegree.advait.findmyflight.service.FlightAwareService;
import com.udacity.nanodegree.advait.findmyflight.service.ServiceFactory;
import com.udacity.nanodegree.advait.findmyflight.util.FlightUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Starting screen to look up for flights between a origin/destination pair.
 */
public class FlightActivityFragment extends Fragment {

    @BindView(R.id.editText)
    EditText originCityText;

    @BindView(R.id.editText2)
    EditText destinationCityText;

    @BindView(R.id.button)
    Button submitButton;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Unbinder unbinder;

    private FlightListAdapter flightListAdapter;

    public FlightActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight, container, false);
        unbinder = ButterKnife.bind(this, view);
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
                service.findFlights(originAirportCode, destinationAirportCode,"nonstop").subscribeOn(Schedulers.newThread())
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
                        }catch(JSONException exception) {

                        }
                    }
                });
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void processFlightDetails(JsonObject flightInfoStatusData) throws JSONException {
        List<Flight> flightList = new ArrayList<>();
        JsonObject findFlightResult = flightInfoStatusData.getAsJsonObject("FindFlightResult");
        JsonArray flightArray = findFlightResult.getAsJsonArray("flights");
        for(int i=0;i<flightArray.size();i++) {
            JsonObject flightJsonObject = flightArray.get(i).getAsJsonObject();
            JsonArray segment = flightJsonObject.getAsJsonArray("segments");
            JsonObject flightSegmentJsonObject = segment.get(0).getAsJsonObject();
            Flight flightObject = new Flight();
            flightObject.populateData(flightSegmentJsonObject);
            flightList.add(flightObject);
        }
        flightListAdapter = new FlightListAdapter(flightList,getContext());
        recyclerView.setAdapter(flightListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
