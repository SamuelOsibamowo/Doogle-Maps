package com.example.dooglemaps.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;
import com.example.dooglemaps.viewModel.Report;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;

public class ReportDetailedActivity extends AppCompatActivity {

    Report report;
    TextView tvDetailedPetDescription;
    TextView tvTypeOfAnimal;
    ImageView ivDetailedReportedPet;
    CardView cvReportMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detailed_report);

        report = (Report) Parcels.unwrap(getIntent().getParcelableExtra(Report.class.getSimpleName())) ;

        cvReportMap = findViewById(R.id.cvReportMap);
        tvTypeOfAnimal = findViewById(R.id.tvTypeOfAnimal);
        tvDetailedPetDescription = findViewById(R.id.tvDetailedPetDescription);
        ivDetailedReportedPet = findViewById(R.id.ivDetailReportedPet);

        tvDetailedPetDescription.setText(report.getDescription());
        tvTypeOfAnimal.setText(report.getAnimal());

        cvReportMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });
        bind();
    }

    private void goToMap() {
        Intent intent = new Intent(ReportDetailedActivity.this, MapActivity.class);
        intent.putExtra("latlng", new LatLng(report.getLat(), report.getLng()));
        startActivity(intent);
    }

    private void bind() {
        Glide.with(this)
                .load(report.getImageUrl())
                .centerCrop()
                .into(ivDetailedReportedPet);
    }

}
