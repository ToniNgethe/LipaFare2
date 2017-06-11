package com.example.toni.lipafare.Operator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toni.lipafare.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.toni.lipafare.R.id.map;

public class Operator extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSIONS_FINE_LOCATION = 1;
    private static final String LOG_TAG = "sdsdasdasdasd";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_2 = 102;

    private GoogleMap mMap;


    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Double myLongitude = null;
    private Double myLatitude = null;

    private static final int GOOGLE_API_CLIENT_ID = 0;

    private TextView text_from, text_to;
    private CardView cd_from, cd_to;

    private FirebaseAuth mAuth;

    private LatLng toCoordinated, fromCoordinates;

    private FloatingActionButton savelocation;

    private String from_address, to_address, from_city, to_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);

        mAuth = FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        //views
        text_from = (TextView) findViewById(R.id.tv_from);
        text_to = (TextView) findViewById(R.id.tv_to);

        cd_from = (CardView) findViewById(R.id.card_from);
        cd_to = (CardView) findViewById(R.id.card_to);

        //google api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //location req
        locationRequest = new LocationRequest();
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //butn
        savelocation = (FloatingActionButton) findViewById(R.id.fab_operator_add);
        savelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        //
        cd_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text_from.setText("From");
                fromToLocation(PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        cd_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_to.setText("To");
                fromToLocation(PLACE_AUTOCOMPLETE_REQUEST_CODE_2);
            }
        });
    }

    private void save() {

        savebg();

    }

    private void savebg() {

        if (!text_to.getText().toString().equals("To") || !text_from.getText().toString().equals("From")) {

            final SweetAlertDialog progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#f50057"));
            progressDialog.setTitleText("Saving sacco route..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //get sacco key

            final DatabaseReference mSacco = FirebaseDatabase.getInstance().getReference().child("Sacco");

            Query q = mSacco.orderByChild("admin").equalTo(mAuth.getCurrentUser().getUid());

            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        final String key = ds.getKey();

                        final DatabaseReference mRoutes = FirebaseDatabase.getInstance().getReference().child("Route");
                        mRoutes.push().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                dataSnapshot.getRef().child("From_city").setValue(from_city);
                                dataSnapshot.getRef().child("From_address").setValue(from_address);
                                dataSnapshot.getRef().child("sacco").setValue(key);
                                dataSnapshot.getRef().child("To_city").setValue(to_city);
                                dataSnapshot.getRef().child("To_address").setValue(to_address).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            //set return route
                                            DatabaseReference cr = mRoutes.push();
                                            cr.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().child("From_city").setValue(to_city);
                                                    dataSnapshot.getRef().child("From_address").setValue(to_address);
                                                    dataSnapshot.getRef().child("sacco").setValue(key);
                                                    dataSnapshot.getRef().child("To_city").setValue(from_city);
                                                    dataSnapshot.getRef().child("To_address").setValue(from_address).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialog.dismiss();
                                                            if (task.isSuccessful()) {

                                                                Intent i = new Intent(Operator.this, PaymentMeansActivity.class);
                                                                i.putExtra("KEY", key);
                                                                startActivity(i);
                                                                finish();
                                                            } else {
                                                                new SweetAlertDialog(Operator.this, SweetAlertDialog.ERROR_TYPE)
                                                                        .setTitleText("Oops..")
                                                                        .setContentText(task.getException().getMessage())
                                                                        .show();
                                                            }
                                                        }
                                                    });
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        } else {
                                            progressDialog.dismiss();
                                            new SweetAlertDialog(Operator.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Oops..")
                                                    .setContentText(task.getException().getMessage())
                                                    .show();

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                progressDialog.dismiss();
                                Toast.makeText(Operator.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    progressDialog.dismiss();
                    Toast.makeText(Operator.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            Toast.makeText(Operator.this, "Choose your From and To route", Toast.LENGTH_SHORT).show();
        }

    }

    private void fromToLocation(int code) {
        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY).setCountry("KE")
                    .build();


            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(Operator.this);

            startActivityForResult(intent, code);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error
            Log.d(LOG_TAG, e.getMessage());

        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Log.d(LOG_TAG, e.getMessage());
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                final Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(LOG_TAG, "Place: " + place.getName());


                text_from.setText(place.getAddress());
                from_address = place.getAddress().toString();
                from_city = place.getName().toString();


                //remove maker fast
                //add marrker to map
                Places.GeoDataApi.getPlaceById(googleApiClient, place.getId())
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getStatus().isSuccess()) {
                                    final Place myPlace = places.get(0);

                                    fromCoordinates = myPlace.getLatLng();

                                    Log.v("Latitude is", "" + fromCoordinates.latitude);
                                    Log.v("Longitude is", "" + fromCoordinates.longitude);

                                    mMap.addMarker(new MarkerOptions().position(fromCoordinates).title(place.getAddress().toString()));
                                    // mMap.animateCamera(CameraUpdateFactory.newLatLng(queriedLocation));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromCoordinates, 15));
                                }
                                places.release();
                            }
                        });


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                // The user canceled the operation.
            }
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_2) {
            if (resultCode == RESULT_OK) {

                final Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(LOG_TAG, "Place: " + place.getName());

                text_to.setText(place.getAddress());
                to_address = place.getAddress().toString();
                to_city = place.getAddress().toString();

                //add marrker to map
                Places.GeoDataApi.getPlaceById(googleApiClient, place.getId())
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getStatus().isSuccess()) {
                                    final Place myPlace = places.get(0);
                                    toCoordinated = myPlace.getLatLng();

                                    Log.v("Latitude is", "" + toCoordinated.latitude);
                                    Log.v("Longitude is", "" + toCoordinated.longitude);

                                    mMap.addMarker(new MarkerOptions().position(toCoordinated).title(place.getAddress().toString()));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toCoordinated, 15));
                                }
                                places.release();
                                connectLine();
                            }
                        });


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                // The user canceled the operation.
            }
        }


    }

    private void connectLine() {

        if (!text_to.getText().equals("To") && !text_from.getText().equals("From")) {

//            DrawRoute.getInstance(this,Operator.this).setFromLatLong(fromCoordinates.latitude,fromCoordinates.longitude)
//                    .setColorHash("#f50057")
//                    .setZoomLevel(8.0F)
//                    .setLoaderMsg("Connecting routes")
//                    .setToLatLong(toCoordinated.latitude,toCoordinated.longitude).setGmapAndKey("AIzaSyAJf2ncu9V5J9SGIpU9pwOjCargOKC18ik",mMap).run();


        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in kenya and move the camera
        LatLng kenya = new LatLng(-0.023559, 37.90619300000003);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kenya));
        mMap.addMarker(new MarkerOptions().position(kenya).title("Kenya"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_FINE_LOCATION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(Operator.this, "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
                    finish();

                }

                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {

        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

    }

    private void requestLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

}
