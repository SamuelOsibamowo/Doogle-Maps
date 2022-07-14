package com.example.dooglemaps.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;
import com.example.dooglemaps.fragments.ApiService;
import com.example.dooglemaps.notifications.Client;
import com.example.dooglemaps.notifications.Data;
import com.example.dooglemaps.notifications.MyResponse;
import com.example.dooglemaps.notifications.Sender;
import com.example.dooglemaps.notifications.Token;
import com.example.dooglemaps.viewModel.Message;
import com.example.dooglemaps.viewModel.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private ImageView pfpImage;
    private TextView username;
    private ImageView btnSend;
    private EditText etSend;
    private Toolbar toolbar;

    private FirebaseUser curUser;
    private DatabaseReference reference;
    private ValueEventListener seenListener;

    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private RecyclerView recyclerView;

    private ApiService apiService;

    private String userId;
    private Boolean notify;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userId = getIntent().getStringExtra("userId");
        notify = false;
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);

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

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
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
                    if (user.getUserId().equals(userId)) {
                        username.setText(user.getUsername());
                        if (!user.getImageUrl().equals("default")) {
                            Glide.with(getApplicationContext())
                                    .load(user.getImageUrl())
                                    .centerCrop()
                                    .into(pfpImage);
                        }
                        readMessages(curUser.getUid(), userId, user.getImageUrl() );
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        seenMessage(userId);

    }

    private void seenMessage(String userId) {
         reference = FirebaseDatabase.getInstance().getReference("chats");
         seenListener = reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for (DataSnapshot childSnap: snapshot.getChildren()) {
                     Message message = childSnap.getValue(Message.class);
                     if (message.getReceiver().equals(curUser.getUid()) && message.getSender().equals(userId)) {
                         HashMap<String, Object> hashMap = new HashMap<>();
                         hashMap.put("isSeen", true);
                         childSnap.getRef().updateChildren(hashMap);
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {}
         });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Message msg = new Message(sender, receiver, message, false);
        reference.child("chats").push().setValue(msg);

        final String msgContent = message;
        reference = FirebaseDatabase.getInstance().getReference("users").child(sender);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msgContent);
                }
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnap: snapshot.getChildren()){
                    Token token = childSnap.getValue(Token.class);
                    Data data = new Data(curUser.getUid(), R.drawable.paw_logo, msg, username, userId);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.i("testing", "sent noti");

                                    }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    @Override
    protected void onPause() {
        reference.removeEventListener(seenListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}