package com.example.dooglemaps.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dooglemaps.R;
import com.example.dooglemaps.view.VPAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment{

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final int REQUEST_CODE = 101;
    private static final long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private static final long FASTEST_INTERVAL = 5000; /* 5 secs */

    private FusedLocationProviderClient fusedLocationProviderClient;
    private double lat, lng;


    private static final String TAG = "HomeFragment";


    public HomeFragment() {}

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

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.vpTab);
        tabLayout.setupWithViewPager(viewPager);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        grabLocation();
        Log.i(TAG, "stop");




    }

    // Method that grabs the users current location and moves the camera to that specific area
    private void grabLocation() {
        // First have to request for permission of the location items I am trying to use
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationCallback locationCallback = new LocationCallback();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    // Grabs the ViewPager
                    VPAdapter vpAdapter = new VPAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                    vpAdapter.addFragment(new ReportFragment(new LatLng(lat, lng)), "Reports");
                    vpAdapter.addFragment(new MissingFragment(new LatLng(lat, lng)), "Missing");
                    vpAdapter.addFragment(new ChatFragment(), "Chat");
                    viewPager.setAdapter(vpAdapter);
                }
            }
        });


    }

}