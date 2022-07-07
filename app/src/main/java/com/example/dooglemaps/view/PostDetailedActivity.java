package com.example.dooglemaps.view;

import android.content.Intent;
import android.os.Bundle;
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

public class PostDetailedActivity extends AppCompatActivity {

    Post post;
    TextView tvDetailedPetDescription;
    ImageView ivDetailedMissingPet;
    CardView cvPostMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detailed_post);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName())) ;
        cvPostMap = findViewById(R.id.cvPostMap);
        tvDetailedPetDescription = findViewById(R.id.tvDetailedPostDescription);
        ivDetailedMissingPet = findViewById(R.id.ivDetailMissingPet);
        tvDetailedPetDescription.setText(post.getDescription());

        cvPostMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap();
            }
        });

        Glide.with(this)
                .load(post.getImageUrl())
                .centerCrop()
                .into(ivDetailedMissingPet);
    }

    private void goToMap() {
        Intent intent = new Intent(PostDetailedActivity.this, MapPostActivity.class);
        intent.putExtra("latlng", new LatLng(post.getLat(), post.getLng()));
        startActivity(intent);
    }
}
