package com.example.dooglemaps.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dooglemaps.R;
import com.example.dooglemaps.dialogs.PostDialog;
import com.example.dooglemaps.dialogs.ReportDialog;
import com.example.dooglemaps.view.MainActivity;
import com.example.dooglemaps.view.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FeedFragment extends Fragment {

    private static final int REQUEST_CODE = 102;


    private ImageView ivSettings;
    private FloatingActionButton fabFeed;


    public FeedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabFeed = view.findViewById(R.id.fabFeed);
        ivSettings = view.findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
        fabFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opening Dialog Fragment
                PostDialog dialog = new PostDialog();
                dialog.setTargetFragment(FeedFragment.this, REQUEST_CODE);
                dialog.show(getFragmentManager(), "PostDialog");

            }
        });
    }

    private void goToSettings() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }


}