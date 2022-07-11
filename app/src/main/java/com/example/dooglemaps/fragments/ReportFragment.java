package com.example.dooglemaps.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dooglemaps.R;
import com.example.dooglemaps.dialogs.ReportDialog;
import com.example.dooglemaps.view.ReportAdapter;
import com.example.dooglemaps.viewModel.Report;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ReportFragment extends Fragment {

    private static final int REQUEST_CODE = 102;

    private Button btnReport;
    private RecyclerView recyclerView;
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

        btnReport = view.findViewById(R.id.btnReport);
        recyclerView = view.findViewById(R.id.rvReports);
        databaseReference = FirebaseDatabase.getInstance().getReference("reports"); //TODO: come back and remove this magic var
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reports = new ArrayList<>();
        reportAdapter = new ReportAdapter(getContext(), reports);
        recyclerView.setAdapter(reportAdapter);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opening Dialog Fragment
                ReportDialog dialog = new ReportDialog(latLng);
                dialog.show(getFragmentManager(), "ReportDialog");
            }
        });

        grabReports();

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