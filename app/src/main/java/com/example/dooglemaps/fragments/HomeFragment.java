package com.example.dooglemaps.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dooglemaps.R;
import com.example.dooglemaps.view.DescriptionDialog;
import com.example.dooglemaps.view.ReportDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment implements ReportDialog.OnInputSelected {

    public static final String TAG = "HomeFragment";
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    FloatingActionButton fabReport;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private static final int REQUEST_CODE = 101;
    double lat, lng;


    private final static int IMAGE_CONSTRAINT = 10;
    private Marker myMarker;
    private Bitmap takenImage;
    private String description;




    public HomeFragment() {}


    // Method that grabs the information sent from the report dialog fragment and uses it to create a new marker on the map
    @Override
    public void sendInput(String input, Bitmap takenImage) {
        MarkerOptions markerOptions = new MarkerOptions();
        this.takenImage = takenImage;
        description = input;
        // Adds marker to the map
        LatLng latLng = new LatLng(lat, lng);
        markerOptions.position(latLng);
        markerOptions.icon(bitmapDescriptor(getContext(), R.drawable.paw_pin ));


        map.clear();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        myMarker = map.addMarker(markerOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get a handle to the fragment and register the callback.
        fabReport = view.findViewById(R.id.fabReport);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        // init the map fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadMap(googleMap);
                }
            });
        }

        fabReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opening Dialog Fragment
                ReportDialog dialog = new ReportDialog();
                dialog.setTargetFragment(HomeFragment.this, REQUEST_CODE);
                dialog.show(getFragmentManager(), "ReportDialog");

            }
        });


    }

    // Helper method that is called when the onMapReady is called (created mainly for cleanliness)
    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerClicked(marker);
                return true;
            }
        });
        getCurrentLocation();
    }

    // Helper method that is called when a marker is clicked (created mainly for cleanliness)
    private void markerClicked(Marker marker) {
        DescriptionDialog dialog = new DescriptionDialog(takenImage, description);
        dialog.setTargetFragment(HomeFragment.this, REQUEST_CODE);
        dialog.show(getFragmentManager(), "DescriptionDialog");
    }


    // Method that sets the size and constraints for the drawable/image that will become the markers new icon
    private BitmapDescriptor bitmapDescriptor(Context context, int imageResId) {
        Drawable drawable = ContextCompat.getDrawable(context, imageResId);
        drawable.setBounds(0,0, drawable.getIntrinsicWidth()/IMAGE_CONSTRAINT, drawable.getIntrinsicHeight()/IMAGE_CONSTRAINT);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()/IMAGE_CONSTRAINT, drawable.getIntrinsicHeight()/IMAGE_CONSTRAINT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }



    // Method that grabs the users current location and moves the camera to that specific area
    private void getCurrentLocation() {
        // First have to request for permission of the location items I am trying to use
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationCallback locationCallback = new LocationCallback();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Location");
                    markerOptions.icon(bitmapDescriptor(getContext(), R.drawable.paw_pin ));
                    // Remove all marker
                    map.clear();
                    // Animating to zoom the marker
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    // Add marker on map
                    map.addMarker(markerOptions);

                }
            }
        });
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (REQUEST_CODE) {
            case REQUEST_CODE:
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
        }

    }

}