package com.bitsplease.rakshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    String TAG = "MyLOGS";
    private static final int RC_SIGN_IN = 1;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String mUsername;
    String mUid,mToken;

    private static int REQUEST_PHONE_CALL=1;
    Button helpButton;
    Button medicalButton;

    @SuppressLint("MissingPermission")
    void makeCall()
    {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "100"));
        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(MainActivity.this, "Calling Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helpButton = findViewById(R.id.help_button);
        ((View) helpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Grant Permission to make calls",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else {
                    makeCall();
                }
            }
        });
        medicalButton= findViewById(R.id.BTNmedical);
        medicalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });


        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    mUsername = user.getDisplayName();
                    mUid = user.getUid();
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }
                                    // Get new Instance ID token
                                    String token = task.getResult().getToken();
                                    mToken = token;
                                    sendDataToFirebase(mUid,token);
                                    Log.d(TAG, "onComplete: "+token);
                                    Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
//                                    // Log and toast
//                                    String msg = getString(R.string.msg_token_fmt, token);
//                                    Log.d(TAG, msg);
//                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                    //sendDataToFirebase(mUid,mToken);
                } else {
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged: User is signed out.");
                    //OnSignedOutInitialise();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    void sendNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_full_open_on_phone)
                        .setContentTitle("Medical Emergency")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    void sendDataToFirebase(String uid,String token)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mUsersDatabaseReference = firebaseDatabase.getReference().child("users");
        mUsersDatabaseReference.child(uid).setValue(token);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
