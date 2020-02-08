package com.bitsplease.rakshak;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    String TAG = "MyLOGS";
    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            Toast.makeText(FirebaseMessagingService.this, remoteMessage.getData().toString(), Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Log.w("Exception", e.toString());
        }
        Log.d(TAG, "onMessageReceived: Recieved"+remoteMessage.getData());
        sendNotification();
        super.onMessageReceived(remoteMessage);
    }

    void sendNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_full_open_on_phone)
                        .setContentTitle("Messages Received")
                        .setContentText("This is a sample notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
