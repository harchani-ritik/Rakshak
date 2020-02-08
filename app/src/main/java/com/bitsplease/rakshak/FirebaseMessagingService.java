package com.bitsplease.rakshak;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
        super.onMessageReceived(remoteMessage);
    }
}
