package com.example.dooglemaps.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.dooglemaps.view.MessageActivity;
import com.example.dooglemaps.view.ReportDetailedActivity;
import com.example.dooglemaps.viewModel.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.parceler.Parcels;



public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessaging";

    private FirebaseUser fuser;
    private DatabaseReference reference;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String sented = message.getData().get("sented");
        String msg = message.getData().get("title");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if ( firebaseUser != null && msg.equals("New Report!")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendOreoMatchingNotification(message);
            } else {
                sendMatchingNotification(message);
            }
        } else if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendOreoNotification(message);
            } else {
                sendNotification(message);
            }
        }
    }

    private void sendMatchingNotification(RemoteMessage message) {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        reference = FirebaseDatabase.getInstance().getReference("reports").child(user).child(body);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Report report = snapshot.getValue(Report.class);
                RemoteMessage.Notification notification = message.getNotification();
                int j = Integer.parseInt(user.replaceAll("[\\D]", ""));

                Intent intent = new Intent(getApplicationContext(), ReportDetailedActivity.class);
                intent.putExtra(Report.class.getSimpleName(), Parcels.wrap(report));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(Integer.parseInt(icon))
                        .setContentTitle(title)
                        .setContentText("Report matching your post found!")
                        .setAutoCancel(true)
                        .setSound(defaultSound)
                        .setContentIntent(pendingIntent);
                NotificationManager noti  =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                int i = 0;
                if (j > 0) {
                    i = j;
                }

                noti.notify(i, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void sendOreoMatchingNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        reference = FirebaseDatabase.getInstance().getReference("reports").child(user).child(body);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Report report = snapshot.getValue(Report.class);

                //RemoteMessage.Notification notification = remoteMessage.getNotification();
                int j = Integer.parseInt(user.replaceAll("[\\D]", ""));

                Intent intent = new Intent(getApplicationContext(), ReportDetailedActivity.class);
                intent.putExtra(Report.class.getSimpleName(), Parcels.wrap(report));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                OreoNotifications oreoNotification = new OreoNotifications(getApplicationContext());
                Notification.Builder builder = oreoNotification.getOreoNotification(title, "Report matching your post found!", pendingIntent,
                        defaultSound, icon);

                int i = 0;
                if (j > 0){
                    i = j;
                }
                oreoNotification.getManager().notify(i, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("userId", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotifications oreoNotification = new OreoNotifications(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if (j > 0){
            i = j;
        }
        oreoNotification.getManager().notify(i, builder.build());

    }

    private void sendNotification(RemoteMessage message) {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("userId", user).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti  =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        noti.notify(i, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();
        if (firebaseUser != null) {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token = new Token(refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }
}
