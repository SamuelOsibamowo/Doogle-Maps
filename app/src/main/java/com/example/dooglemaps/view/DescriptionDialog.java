package com.example.dooglemaps.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dooglemaps.R;

public class DescriptionDialog extends DialogFragment {

    private static final String TAG = "DescriptionDialog";
    private ImageView ivAnimalImage;
    private TextView tvDescription;
    private Bitmap bitmap;
    private String description;

    public DescriptionDialog(Bitmap bitmap, String description) {
        this.bitmap = bitmap;
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
        ivAnimalImage.setImageBitmap(bitmap);
    }

}
