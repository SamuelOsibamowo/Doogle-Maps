package com.example.dooglemaps.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReportDetailedActivity extends AppCompatActivity {

    Report report;
    TextView tvDetailedPetDescription, tvTypeOfAnimal, tvLocation, tvShare;
    ImageView ivDetailedReportedPet;
    CardView cvReportMap, cvStartReportChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detailed_report);

        report = (Report) Parcels.unwrap(getIntent().getParcelableExtra(Report.class.getSimpleName()));

        tvShare = findViewById(R.id.tvShare);
        cvStartReportChat = findViewById(R.id.cvStartReportChat);
        cvReportMap = findViewById(R.id.cvReportMap);
        tvTypeOfAnimal = findViewById(R.id.tvTypeOfAnimal);
        tvDetailedPetDescription = findViewById(R.id.tvDetailedPetDescription);
        ivDetailedReportedPet = findViewById(R.id.ivDetailReportedPet);
        tvLocation = findViewById(R.id.tvLocation);

        tvDetailedPetDescription.setText(report.getDescription());
        tvTypeOfAnimal.setText(report.getAnimal());
        tvLocation.setText(grabAddress());
        bind();

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShare();
            }
        });

        cvReportMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        cvStartReportChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChat();
            }
        });
    }

    private void goToShare() {
        BitmapDrawable drawable = (BitmapDrawable) ivDetailedReportedPet.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
        Uri uri = Uri.parse(bitmapPath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Reported Animal: " + report.getDescription());
        startActivity(Intent.createChooser(intent, "Share"));

    }

    private void goToChat() {
        Intent intent = new Intent(ReportDetailedActivity.this, MessageActivity.class);
        String uid = report.getUserId();
        intent.putExtra("userId", uid);
        startActivity(intent);
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

    private String grabAddress() {
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(report.getLat(), report.getLng(), 1);
            if (addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

}
