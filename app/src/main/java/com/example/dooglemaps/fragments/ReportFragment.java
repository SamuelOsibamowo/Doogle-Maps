package com.example.dooglemaps.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.dooglemaps.R;
import com.example.dooglemaps.view.ReportAdapter;
import com.example.dooglemaps.view.ReportCreationActivity;
import com.example.dooglemaps.viewModel.Report;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ReportFragment extends Fragment {

    private static final String PATH = "reports";



    private FloatingActionButton btnReport;
    private RecyclerView recyclerView;
    private SearchView svFilter;


    private DatabaseReference databaseReference;
    private ReportAdapter reportAdapter;
    private ArrayList<Report> reports;
    private LatLng latLng;




    public ReportFragment(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        svFilter = view.findViewById(R.id.svFilter);
        btnReport = view.findViewById(R.id.btnReport);
        recyclerView = view.findViewById(R.id.rvReports);

        databaseReference = FirebaseDatabase.getInstance().getReference(PATH);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reports = new ArrayList<>();
        reportAdapter = new ReportAdapter(getContext(), reports);
        recyclerView.setAdapter(reportAdapter);

        svFilter.clearFocus();
        svFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterReportsSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterReportsSearch(newText);
                return false;
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ReportCreationActivity.class);
                intent.putExtra("latlng", latLng);
                startActivity(intent);
            }
        });
        grabReports();

    }

    private void filterReportsSearch(String newText) {
        reports.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Report> filter1 = new ArrayList<>();
                ArrayList<Report> filter2 = new ArrayList<>();
                ArrayList<Report> filter3 = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                        Report report = childSnapShot.getValue(Report.class);
                        String address = "";
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(report.getLat(), report.getLng(),1);
                            if (addressList.size() > 0) {
                                address = addressList.get(0).getAddressLine(0);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (address.contains(newText)) {
                            filter1.add(report);
                        } else if (report.getAnimal().contains(newText)) {
                            filter2.add(report);
                        } else if (report.getDescription().contains(newText)) {
                            filter3.add(report);
                        }
                    }
                }
                reports.addAll(filter1);
                reports.addAll(filter2);
                reports.addAll(filter3);
                reportAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void grabReports() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                        Report report = childSnapShot.getValue(Report.class);
                        reports.add(report);
                    }
                    reportAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}