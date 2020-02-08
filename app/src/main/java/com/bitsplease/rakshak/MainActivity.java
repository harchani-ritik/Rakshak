package com.bitsplease.rakshak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
//import android.telecom.Call;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
//import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    String TAG = "MyLOGS";
    String emergencyType = "general";
    private static final int RC_SIGN_IN = 1;
    static String mLat,mLon;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private FusedLocationProviderClient fusedLocationClient;
    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String mUsername;
    String mUid,mToken;
    String lat, lon;
    private static int REQUEST_PHONE_CALL=1;
    Button helpButton;
    Button generalButton,medicalButton,fireButton,disasterButton;

    @SuppressLint("MissingPermission")
    void makeCall(String type) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: got location");

                if (location != null) {
                    // Logic to handle location
                    lat = location.getLatitude() + "";
                    lon = location.getLongitude() + "";
                    Log.d(TAG, "onSuccess: Location found");
                    Log.d(TAG, "onSuccess: Lat is "+lat+"Long is "+lon);
                }
            }
        });
        String url = getResources().getString(R.string.server) + "requests";
        RequestBody body = new FormBody.Builder()
                .add("uid", mUid)
                .add("loc", lat + " " + lon)
                .add("type", "fire")
                .add("msg","")
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Toast.makeText(getApplicationContext(), "Trying to Login", Toast.LENGTH_LONG).show();

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
        String phoneNumber;
        if(type.equalsIgnoreCase("general"))
        {
            phoneNumber=getString(R.string.general_call);
        }
        else if(type.equalsIgnoreCase("fire")){
            phoneNumber=getString(R.string.fire_call);
        }
        else if (type.equalsIgnoreCase("medical")){
            phoneNumber=getString(R.string.medical_call);
        }
        else {
            phoneNumber=getString(R.string.disaster_call);
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                else
                {
                    makeCall(emergencyType);
                }
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: got location");
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    String mlat = location.getLatitude() + "";
                    String mlong = location.getLongitude() + "";
//                              BTNsend.setEnabled(true);
//                                Toast.makeText(Emergency.this, "Selected is " + select, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSuccess: Location found");
                    Log.d(TAG, "onSuccess: Lat is "+mlat+"Long is "+mlong);
                    mLat=mlat;mLon=mlong;
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }

        helpButton = findViewById(R.id.BTNhelp);
        ((View) helpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Grant Permission to make calls",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else {
                    makeCall(emergencyType);
                }
            }
        });
        generalButton= findViewById(R.id.BTNgeneral);
        fireButton = findViewById(R.id.BTNfire);
        medicalButton= findViewById(R.id.BTNmedical);
        disasterButton=findViewById(R.id.BTNdisaster);

        generalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"General Emergency Enabled",Toast.LENGTH_SHORT).show();
                emergencyType="general";
            }
        });
        fireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Fire Emergency Enabled",Toast.LENGTH_SHORT).show();
                emergencyType="fire";
            }
        });
        medicalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Medical Emergency Enabled",Toast.LENGTH_SHORT).show();
                emergencyType="medical";
            }
        });
        disasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Disaster Alert Enabled",Toast.LENGTH_SHORT).show();
                emergencyType="disaster";
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
                                }
                            });
                } else {
                    // user is signed out
                    Log.d(TAG, "onAuthStateChanged: User is signed out.");
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
