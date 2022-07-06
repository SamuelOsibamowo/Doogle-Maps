package com.example.dooglemaps.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;

public class DescriptionDialog extends DialogFragment {

    private static final String TAG = "DescriptionDialog";
    private ImageView ivAnimalImage;
    private TextView tvDescription;
    private String image;
    private String description;

    public DescriptionDialog(String image, String description) {
        this.image = image;
        this.description = description;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_description, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAnimalImage = view.findViewById(R.id.ivAnimalImage);
        tvDescription = view.findViewById(R.id.tvDescription);

        tvDescription.setText(description);

        Glide.with(getContext())
                .load(image)
                .into(ivAnimalImage);
    }

}
