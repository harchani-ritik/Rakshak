package com.bitsplease.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bitsplease.rakshak.MainActivity.mUid;
import static com.bitsplease.rakshak.MainActivity.organisation;

public class InNetwork extends AppCompatActivity {

    Spinner spinner;
    OkHttpClient client;
    String TAG = "MyLOGS";
    Button BTNaskhelp,BTNInNetCommunity, BTNInNetPredictor;
    String Emergency="General Emergency";
    static String mLat,mLon;
    String lat, lon;
    private FusedLocationProviderClient fusedLocationClient;
    String mToken;
    protected void getloc(){
        super.onStart();
        fusedLocationClient.getLastLocation().addOnSuccessListener(InNetwork.this, new OnSuccessListener<Location>() {
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
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_network);
        client = new OkHttpClient();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getloc();
        spinner = findViewById(R.id.spinner);
        BTNaskhelp=findViewById(R.id.BTNaskhelp);
        BTNInNetCommunity=findViewById(R.id.BTNInNetCommunity);
        BTNInNetPredictor=findViewById(R.id.BTNInNetPredictor);
        BTNInNetPredictor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                        final String currentTime = Calendar.getInstance().getTime().toString().split(" ")[3];

                        RequestBody body = new FormBody.Builder()
                                .add("military_time",  currentTime.substring(0, currentTime.lastIndexOf(':')))
                                .add("lat", mLat)
                                .add("long", mLon)
                                .add("age", "25")
                                .add("gemder", "female")
                                .build();



                        Request request = new Request.Builder()
                                .url(getResources().getString(R.string.server) + "predict")
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
                                                try {
                                                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
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
            }
        });
        BTNInNetCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InNetwork.this);
                builder.setTitle("Enter your organisation network ID key");

                final EditText input = new EditText(InNetwork.this);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestBody body = new FormBody.Builder()
                                .add("uid", mUid)
                                .add("key", input.getText().toString())
                                .build();



                        Request request = new Request.Builder()
                                .url(getResources().getString(R.string.server) + "usenetwork")
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
                                                        try {
                                                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
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
                        organisation = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        String[] spinnerEmergencylist = {"General Emergency", "Medical", "Fire","Disaster"};
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

        String url = getResources().getString(R.string.server) + "requests";
        RequestBody body = new FormBody.Builder()
                .add("uid", mUid)
                .add("loc", mLat + " " + mLon)
                .add("type", type)
                .add("msg","")
                .build();



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
                                try {
                                    Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
