package com.example.toni.lipafare.Passanger.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toni.lipafare.Passanger.PassangerSearchResults;
import com.example.toni.lipafare.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by toni on 4/28/17.
 */

public class PassangerHomeFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String FROM_LOCATION_CITY = "FROM_LOCATION_CITY";
    public static final String FROM_LOCATION_ADD = "FROM_LOCATION_ADD";
    public static final String TO_LOCATION_CITY = "TO_LOCATION_CITY";
    public static final String TO_LOCATION_ADD = "TO_LOCATION_ADD";
    private static final String LOG_TAG = PassangerHomeFragment.class.getSimpleName();
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_2 = 10;
    private View mView;
    private ImageView _from, _to, _date;
    private TextView loc_from, loc_to, loc_date;
    private Button submit;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleApiClient googleApiClient;
    private LatLng toCoordinated, fromCoordinates;

    private String from_address, from_city, to_address, to_city;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_passangerhome, container, false);

        //views
        _from = (ImageView) mView.findViewById(R.id.iv_fragmenthome_from);
        _to = (ImageView) mView.findViewById(R.id.iv_fragmenthome_to);
        _date = (ImageView) mView.findViewById(R.id.iv_passangerhome_calenda);
        loc_from = (TextView) mView.findViewById(R.id.tv_passangerhome_from);
        loc_to = (TextView) mView.findViewById(R.id.tv_passangerhome_to);
        loc_date = (TextView) mView.findViewById(R.id.tv_passangerhome_date);
        submit = (Button) mView.findViewById(R.id.btn_passangerhome_search);

        //attach listeners
        _from.setOnClickListener(this);
        _to.setOnClickListener(this);
        _date.setOnClickListener(this);
        submit.setOnClickListener(this);

        //
        //google api client
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(this)
                .build();


        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();

        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {

        if (v == _from) {

            //lunch googles auto complete
            try {

                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(Place.TYPE_COUNTRY).setCountry("KE")
                        .build();

                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(typeFilter)
                                .build(getActivity());

                PassangerHomeFragment.this.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
                Log.v(LOG_TAG, e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                Log.v(LOG_TAG, e.getMessage());
            }


        } else if (v == _to) {

            //lunch googles auto complete
            try {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(Place.TYPE_COUNTRY).setCountry("KE")
                        .build();

                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setFilter(typeFilter)
                                .build(getActivity());
                PassangerHomeFragment.this.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_2);

            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
                Log.v(LOG_TAG, e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                Log.v(LOG_TAG, e.getMessage());
            }

        } else if (v == _date) {

            //get date edittext
            showDatePicker();

        } else if (v == submit) {
            submitQuery();
        }

    }

    private void submitQuery() {

        if (!loc_from.getText().toString().equals("FROM")) {
            if (!loc_to.getText().toString().equals("TO")) {

                //open search activity
                Intent search = new Intent(getActivity(), PassangerSearchResults.class);
                search.putExtra(FROM_LOCATION_ADD, from_address);
                search.putExtra(FROM_LOCATION_CITY, from_city);
                search.putExtra(TO_LOCATION_ADD, to_address);
                search.putExtra(TO_LOCATION_CITY, to_city);
                startActivity(search);

            } else {
                Toast.makeText(getActivity(), "Choose your 'To' Location", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Choose your 'From' Location", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        java.util.Calendar calender = java.util.Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            loc_date.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1)
                    + "-" + String.valueOf(year));
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    final Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    Log.i(LOG_TAG, "Place: " + place.getName() + place.getId() + place.getLatLng());

                    loc_from.setText(place.getAddress());

                    from_address = place.getAddress().toString();
                    from_city = place.getName().toString();

                }

            } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_2) {

                final Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(LOG_TAG, "Place: " + place.getName());

                loc_to.setText(place.getAddress());

                to_address = place.getAddress().toString();
                to_city = place.getName().toString();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());
            }
        } catch (Exception e) {
            Log.v(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection error:" + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
        }
    }
}



