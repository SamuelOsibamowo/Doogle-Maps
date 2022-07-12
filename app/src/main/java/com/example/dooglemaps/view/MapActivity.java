package com.example.dooglemaps.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import com.example.dooglemaps.R;
import com.example.dooglemaps.dialogs.DescriptionDialog;
import com.example.dooglemaps.fragments.HomeFragment;
import com.example.dooglemaps.viewModel.Report;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {

    private static final String DATABASE_REPORT_PATH = "reports";
    private static final int REQUEST_CODE = 101;
    private static final long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private static final long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static int IMAGE_CONSTRAINT = 10;


    private Bundle extras;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference reference;
    private BitmapDescriptor pawPinDescriptor;
    private LatLng curMarkerLoc;

    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        extras = getIntent().getExtras();
        curMarkerLoc = (LatLng) extras.get("latlng");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        reference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REPORT_PATH);
        pawPinDescriptor = bitmapDescriptor(this, R.drawable.paw_pin, IMAGE_CONSTRAINT);
        // init the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadMap(googleMap);
                }
            });
        }

    }


    // Helper method that is called when the onMapReady is called (created mainly for cleanliness)
    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        grabLocationAndMarkers();
    }



    // Method that grabs the users current location and moves the camera to that specific area
    private void grabLocationAndMarkers() {
        // First have to request for permission of the location items I am trying to use
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
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
                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .visible(false);
                    map.clear();
                    map.addMarker(markerOptions);
                    grabReports();
                    grabReportScreens();
                }
            }
        });
    }

    // Helper method that is called when a marker is clicked (created mainly for cleanliness)
    private void markerClicked(Marker marker, String image, String description) {
        DescriptionDialog dialog = new DescriptionDialog(image, description);
        dialog.show(getSupportFragmentManager(), "DescriptionDialog");
    }

    private void grabReportScreens() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Goes through each of the reports
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                                Report report = childSnapShot.getValue(Report.class);
                                String description = report.getDescription();
                                String reportId = report.getReportId();
                                String image = report.getImageUrl();
                                if (reportId.equals(marker.getTitle())) {
                                    markerClicked(marker, image, description);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                return false;
            }
        });
    }

    // Grabs the current reports and displays them to the map
    public void grabReports() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Goes through each of the reports
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                        Report report = childSnapShot.getValue(Report.class);
                        LatLng latLng = new LatLng(report.getLat(), report.getLng());
                        MarkerOptions markerOptions=new MarkerOptions()
                                .title(report.getReportId())
                                .position(latLng)
                                .visible(true)
                                .icon(pawPinDescriptor);
                        map.addMarker(markerOptions);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curMarkerLoc,16));
    }


    // Method that sets the size and constraints for the drawable/image that will become the markers new icon
    private BitmapDescriptor bitmapDescriptor(Context context, int imageResId, int imageConstraint) {
        Drawable drawable = ContextCompat.getDrawable(context, imageResId);
        drawable.setBounds(0,0, drawable.getIntrinsicWidth()/imageConstraint, drawable.getIntrinsicHeight()/imageConstraint);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()/imageConstraint, drawable.getIntrinsicHeight()/imageConstraint, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }





}