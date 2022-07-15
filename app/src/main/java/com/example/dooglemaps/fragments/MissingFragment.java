package com.example.dooglemaps.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.dooglemaps.R;
import com.example.dooglemaps.view.PostAdapter;
import com.example.dooglemaps.view.PostCreationActivity;
import com.example.dooglemaps.viewModel.Post;
import com.example.dooglemaps.viewModel.Report;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MissingFragment extends Fragment {

    private static final String PATH = "posts";

    private FloatingActionButton btnPost;
    private RecyclerView recyclerView;
    private SearchView svFilter;


    private DatabaseReference databaseReference;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts;
    private LatLng latLng;


    public MissingFragment(LatLng latLng) {
        this.latLng = latLng;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_missing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPost = view.findViewById(R.id.btnPost);
        recyclerView = view.findViewById(R.id.rvFeed);
        svFilter = view.findViewById(R.id.svFilter);

        databaseReference = FirebaseDatabase.getInstance().getReference(PATH);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(postAdapter);

        svFilter.clearFocus();
        svFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPostsSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPostsSearch(newText);
                return false;
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PostCreationActivity.class);
                intent.putExtra("latlng", latLng);
                startActivity(intent);

            }
        });
        grabPosts();
    }

    private void filterPostsSearch(String newText) {
        posts.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Post> filter1 = new ArrayList<>();
                ArrayList<Post> filter2 = new ArrayList<>();
                ArrayList<Post> filter3 = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                        Post post = childSnapShot.getValue(Post.class);
                        String address = "";
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(post.getLat(), post.getLng(),1);
                            if (addressList.size() > 0) {
                                address = addressList.get(0).getAddressLine(0);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (address.contains(newText)) {
                            // Filter that focuses on animal type
                            filter1.add(post);
                        } else if (post.getAnimal().contains(newText)) {
                            // Filter that focuses on post description
                            filter2.add(post);
                        } else if (post.getDescription().contains(newText)) {
                            filter3.add(post);

                        }
                        // TODO: Add third filter that focuses on address!
                    }
                }
                posts.addAll(filter1);
                posts.addAll(filter2);
                posts.addAll(filter3);
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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