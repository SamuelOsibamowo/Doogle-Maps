package com.example.dooglemaps.view;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;
import com.example.dooglemaps.viewModel.Post;
import com.example.dooglemaps.viewModel.Report;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostDetailedActivity extends AppCompatActivity {

    Post post;
    TextView tvDetailedPetDescription, tvPostTypeOfAnimal, tvLocation, tvShare;
    ImageView ivDetailedMissingPet;
    CardView cvPostMap, cvStartPostChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detailed_post);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvShare = findViewById(R.id.tvShare);
        cvStartPostChat = findViewById(R.id.cvStartPostChat);
        cvPostMap = findViewById(R.id.cvPostMap);
        tvDetailedPetDescription = findViewById(R.id.tvDetailedPostDescription);
        ivDetailedMissingPet = findViewById(R.id.ivDetailMissingPet);
        tvPostTypeOfAnimal = findViewById(R.id.tvPostTypeOfAnimal);
        tvLocation = findViewById(R.id.tvLocation);

        tvDetailedPetDescription.setText(post.getDescription());
        tvPostTypeOfAnimal.setText(post.getAnimal());
        tvLocation.setText(grabAddress());

        bind();

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShare();
            }
        });

        cvPostMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        cvStartPostChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChat();
            }
        });



    }

    private void goToShare() {
        BitmapDrawable drawable = (BitmapDrawable) ivDetailedMissingPet.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
        Uri uri = Uri.parse(bitmapPath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Missing Animal: " + post.getDescription());
        startActivity(Intent.createChooser(intent, "Share"));

    }

    private void goToChat() {
        Intent intent = new Intent(PostDetailedActivity.this, MessageActivity.class);
        intent.putExtra("userId", post.getUserId());
        startActivity(intent);
    }

    private void goToMap() {
        Intent intent = new Intent(PostDetailedActivity.this, MapPostActivity.class);
        intent.putExtra("latlng", new LatLng(post.getLat(), post.getLng()));
        startActivity(intent);
    }

    private String grabAddress(){
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(post.getLat(), post.getLng(),1);
            if (addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void bind(){
        Glide.with(this)
                .load(post.getImageUrl())
                .centerCrop()
                .into(ivDetailedMissingPet);
    }
}
