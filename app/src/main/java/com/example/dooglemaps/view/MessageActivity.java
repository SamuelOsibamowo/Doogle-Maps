package com.example.dooglemaps.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;
import com.example.dooglemaps.viewModel.Message;
import com.example.dooglemaps.viewModel.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private ImageView pfpImage;
    private TextView username;
    private ImageView btnSend;
    private EditText etSend;
    private Toolbar toolbar;

    private FirebaseUser curUser;
    private DatabaseReference reference;
    private Intent intent;

    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        intent = getIntent();

        String userId = intent.getStringExtra("userId");
        toolbar = findViewById(R.id.toolbar);
        pfpImage = findViewById(R.id.profileImage);
        username = findViewById(R.id.username);
        btnSend = findViewById(R.id.btnSendMessage);
        etSend = findViewById(R.id.etSend);

        recyclerView = findViewById(R.id.rvChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etSend.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(curUser.getUid(), userId, message);
                } else {
                    Toast.makeText(MessageActivity.this, "You cant send an empty message", Toast.LENGTH_SHORT).show();
                }
                etSend.setText("");
            }
        });

        curUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                    User user = childSnapShot.getValue(User.class);
                    username.setText(user.getUsername());
                    // TODO: Set up a profile place that allows the user to change their pfp
                    Glide.with(MessageActivity.this)
                            .load(R.drawable.profile_icon)
                            .into(pfpImage);

                    readMessages(curUser.getUid(), userId, "" ); // TODO: Replace with actual image profile string

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Message msg = new Message(sender, receiver, message);
        reference.child("chats").push().setValue(msg);
    }

    private void readMessages(String myId, String userId, String imageUrl) {
        messages = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot childSnapShot: snapshot.getChildren()) {
                    Message message = childSnapShot.getValue(Message.class);
                    if (message.getReceiver().equals(myId) && message.getSender().equals(userId) ||
                            message.getReceiver().equals(userId) &&  message.getSender().equals(myId)) {

                        messages.add(message);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, messages, imageUrl);
                    recyclerView.setAdapter(messageAdapter);

                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
 }