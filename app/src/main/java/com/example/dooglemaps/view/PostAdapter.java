package com.example.dooglemaps.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;
import com.example.dooglemaps.model.Post;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private static final String TAG = "PostAdapter";
    Context context;
    ArrayList<Post> posts;

    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false );
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = posts.get(position);
        Log.i(TAG, "Image URL: " + post.getImageUrl() + " Post Description: " + post.getDescription());
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMissingPet;
        TextView tvPetDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPetDescription = itemView.findViewById(R.id.tvPetDescription);
            ivMissingPet = itemView.findViewById(R.id.ivMissingPet);



        }

        public void bind(Post post) {
            tvPetDescription.setText(post.getDescription());
            //Log.i(TAG, "Image URL: " + post.getImageUrl() + " Post Descrip: "+ post.getDescription());
            Glide.with(context)
                    .load(post.getImageUrl())
                    .into(ivMissingPet);
        }
    }
}
