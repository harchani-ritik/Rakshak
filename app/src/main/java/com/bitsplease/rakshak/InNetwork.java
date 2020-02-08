package com.bitsplease.rakshak;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bitsplease.rakshak.MainActivity.mUid;

public class InNetwork extends AppCompatActivity {

    Spinner spinner;
    String TAG = "MyLOGS";
    Button BTNaskhelp;
    String Emergency="General Emergency";
    static String mLat,mLon;
    String lat, lon;
    private FusedLocationProviderClient fusedLocationClient;
    String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_network);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(InNetwork.this, new OnSuccessListener<Location>() {
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

        spinner = findViewById(R.id.spinner);
        BTNaskhelp=findViewById(R.id.BTNaskhelp);
        String[] spinnerEmergencylist = {"General Emergency", "Medical", "Fire"};
        ArrayAdapter<String> spinnerEmergencyAdapter = new ArrayAdapter<>(InNetwork.this, android.R.layout.simple_list_item_1, spinnerEmergencylist);
        spinnerEmergencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerEmergencyAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("General Emergency")){
                    Emergency="General Emergency";
//                    Toast.makeText(InNetwork.this, "General Emergency selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, "onItemSelected: in else");
                    Toast.makeText(InNetwork.this, parent.getItemAtPosition(position).toString()+" selected.", Toast.LENGTH_SHORT).show();
                    Emergency=parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: nothing selected");
                Toast.makeText(InNetwork.this, "", Toast.LENGTH_SHORT).show();
            }
        });
        BTNaskhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(Emergency);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @SuppressLint("MissingPermission")
    void makeCall(String type) {
        fusedLocationClient.getLastLocation().addOnSuccessListener(InNetwork.this, new OnSuccessListener<Location>() {
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
                        InNetwork.this.runOnUiThread(new Runnable() {
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
        if(type.equalsIgnoreCase("General Emergency"))
        {
            phoneNumber=getString(R.string.general_call);
        }
        else if(type.equalsIgnoreCase("Fire")){
            phoneNumber=getString(R.string.fire_call);
        }
        else if (type.equalsIgnoreCase("Medical")){
            phoneNumber=getString(R.string.medical_call);
        }
        else {
            phoneNumber=getString(R.string.disaster_call);
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

}