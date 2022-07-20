package com.example.dooglemaps.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

public class PostDetailedActivity extends AppCompatActivity {

    Post post;
    TextView tvDetailedPetDescription, tvShare;
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

        tvDetailedPetDescription.setText(post.getDescription());

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

        Glide.with(this)
                .load(post.getImageUrl())
                .centerCrop()
                .into(ivDetailedMissingPet);

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
}
