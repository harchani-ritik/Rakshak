package com.bitsplease.rakshak;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

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

        String emergencyType,title,message;
        emergencyType=remoteMessage.getData().get("type");
        assert emergencyType != null;
        if (emergencyType.equalsIgnoreCase("general"))
        {
            title="General Emergency";
            message=getString(R.string.general_message);
        }
        else if(emergencyType.equalsIgnoreCase("fire"))
        {
            title="Fire Alert";
            message=getString(R.string.fire_message);
        }
        else if (emergencyType.equalsIgnoreCase("medical"))
        {
            title="Medical Help";
            message=getString(R.string.medical_message);
        }
        else{
            title="Disaster Alert";
            message=getString(R.string.disaster_message);
        }
        try {
            String[] location = Objects.requireNonNull(remoteMessage.getData().get("loc")).split(" ", 2);
            double distance = getDistanceFromEmergency(location[0], location[1]);
            message = message + "\nEmergency received at distance = " + distance + " KM";
            String dis = String.valueOf(distance).substring(0, 5);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Exception encountered" + e);
        }
            showNotification(FirebaseMessagingService.this, title + " (" + "0.100" + "KM)", message,"25.7833","81.7723");
            Log.d(TAG, "Message=" + message);
            super.onMessageReceived(remoteMessage);


    }

    double getDistanceFromEmergency(String lat1,String lon1)
    {
        String lat2= MainActivity.mLat;
        String lon2 = MainActivity.mLon;
        return distance(Double.parseDouble(lat1),Double.parseDouble(lon1),Double.parseDouble(lat2),Double.parseDouble(lon2));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void showNotification(Context context, String title, String body,String lat,String lon) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon+"?z=15");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(mapIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
