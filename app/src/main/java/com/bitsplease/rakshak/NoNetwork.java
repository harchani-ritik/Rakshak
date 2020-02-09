package com.bitsplease.rakshak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.models.ChirpError;

import static com.bitsplease.rakshak.CustomDialogClass.CHIRP_APP_CONFIG;
import static com.bitsplease.rakshak.CustomDialogClass.CHIRP_APP_KEY;
import static com.bitsplease.rakshak.CustomDialogClass.CHIRP_APP_SECRET;

public class NoNetwork extends AppCompatActivity {

    Button BTNemergencyMSG,BTNwalkietalkie;
    public static ChirpSDK chirp;
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;
    private FusedLocationProviderClient fusedLocationClient;
    public static String mLat,mLon;
    String TAG = "MyLOGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(NoNetwork.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: got location");
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    String mlat = location.getLatitude() + "";
                    String mlong = location.getLongitude() + "";
                    Log.d(TAG, "onSuccess: Location found");
                    Log.d(TAG, "onSuccess: Lat is "+mlat+"Long is "+mlong);
                    mLat=mlat;mLon=mlong;
                }
            }
        });

        chirp = new ChirpSDK(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
        ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
        if (error.getCode() == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK");
        } else {
            Log.e("ChirpError: ", error.getMessage());
        }

        BTNemergencyMSG=findViewById(R.id.BTNemergencyMSG);
        BTNwalkietalkie=findViewById(R.id.BTNwalkietalkie);
        BTNemergencyMSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClass cdd = new CustomDialogClass(NoNetwork.this);
                cdd.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
        else {
            ChirpError error = chirp.start(true, true);
            if (error.getCode() > 0) {
                Log.e("ChirpError: ", error.getMessage());
            } else {
                Log.v("ChirpSDK: ", "Started ChirpSDK");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ChirpError error = chirp.start();
                    if (error.getCode() > 0) {
                        Log.e("ChirpError: ", error.getMessage());
                    } else {
                        Log.v("ChirpSDK: ", "Started ChirpSDK");
                    }
                }
                return;
            }
        }
    }
}
