package com.example.dooglemaps.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dooglemaps.R;
import com.example.dooglemaps.dialogs.PostDialog;
import com.example.dooglemaps.viewModel.Post;
import com.example.dooglemaps.view.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FeedFragment extends Fragment {

    private static final int REQUEST_CODE = 102;

    private FloatingActionButton fabFeed;

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    PostAdapter postAdapter;
    ArrayList<Post> posts;


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
        recyclerView = view.findViewById(R.id.rvFeed);
        databaseReference = FirebaseDatabase.getInstance().getReference("posts"); //TODO: come back and remove this magic var
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(postAdapter);

        fabFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opening Dialog Fragment
                PostDialog dialog = new PostDialog();
                dialog.setTargetFragment(FeedFragment.this, REQUEST_CODE);
                dialog.show(getFragmentManager(), "PostDialog");

            }
        });

        grabPosts();

    }

    private void grabPosts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                        Post post = childSnapShot.getValue(Post.class);

                        posts.add(post);


                    }
                    postAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}