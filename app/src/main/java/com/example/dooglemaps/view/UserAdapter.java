package com.example.dooglemaps.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;
import com.example.dooglemaps.viewModel.Message;
import com.example.dooglemaps.viewModel.Report;
import com.example.dooglemaps.viewModel.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.parceler.Parcels;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{
    private static final String TAG = "UserAdapter";

    private Context context;
    private List<User> users;
    private String theLastMessage;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getUsername());
        if (!user.getImageUrl().equals("default")) {
            Glide.with(context)
                    .load(user.getImageUrl())
                    .centerCrop()
                    .into(holder.profileImage);
        }
        getLastMessage(user.getUserId(), holder.lastMessage);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView username, lastMessage;
        public ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            username = itemView.findViewById(R.id.username);
            profileImage = itemView.findViewById(R.id.profileImage);
            lastMessage = itemView.findViewById(R.id.lastMessage);


        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                User user = users.get(position);
                Intent intent = new Intent(context, MessageActivity.class);
                String uid = user.getUserId();
                intent.putExtra("userId", uid);
                context.startActivity(intent);
            }
        }
    }

    private void getLastMessage(String userId, TextView lastMsg) {
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnap: snapshot.getChildren()) {
                    Message message = childSnap.getValue(Message.class);
                    if (message.getReceiver().equals(firebaseUser.getUid()) && message.getSender().equals(userId) ||
                            message.getReceiver().equals(userId) && message.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = message.getMessage();

                        switch (theLastMessage) {
                            case "default":
                                lastMsg.setText("No Message");
                                break;
                            default:
                                lastMsg.setText(theLastMessage );
                                break;
                        }
                        theLastMessage = "default";
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
