package com.example.dooglemaps.view;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private static final String TAG = "MessageAdapter";
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;


    private Context context;
    private List<Message> messages;
    private String imageUrl;

    private FirebaseUser user;

    public MessageAdapter(Context context, List<Message> messages, String imageUrl) {
        this.context = context;
        this.messages = messages;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.showMessage.setText(message.getMessage());
        if (!imageUrl.equals("default")) {
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.profileImage);
        }
        if (position == messages.size()-1 ) {
            if (message.getIsSeen()) {
                holder.isSeen.setText("Seen");
            } else {
                holder.isSeen.setText("Delivered");
            }
        } else {
            holder.isSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (messages.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView showMessage, isSeen;
        ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImage = itemView.findViewById(R.id.profileImage);
            isSeen = itemView.findViewById(R.id.tvSeen);


        }
    }
}
