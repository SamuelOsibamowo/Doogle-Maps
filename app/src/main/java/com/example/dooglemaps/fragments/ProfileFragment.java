package com.example.dooglemaps.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dooglemaps.R;
import com.example.dooglemaps.viewModel.Post;
import com.example.dooglemaps.view.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE = 102;

    private FloatingActionButton fabFeed;

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    PostAdapter postAdapter;
    ArrayList<Post> posts;


    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }




}