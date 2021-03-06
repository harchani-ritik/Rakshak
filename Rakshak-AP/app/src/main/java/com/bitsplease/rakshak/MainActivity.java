package com.bitsplease.rakshak;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.io.IOException;
import java.util.Arrays;

import io.chirp.chirpsdk.ChirpSDK;
import io.chirp.chirpsdk.models.ChirpError;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bitsplease.rakshak.CustomDialogClass.CHIRP_APP_CONFIG;
import static com.bitsplease.rakshak.CustomDialogClass.CHIRP_APP_KEY;
import static com.bitsplease.rakshak.CustomDialogClass.CHIRP_APP_SECRET;
import static com.bitsplease.rakshak.NoNetwork.chirp;

//import android.telecom.Call;
//import com.google.android.gms.common.api.Response;

public class MainActivity extends AppCompatActivity {

    String TAG = "MyLOGS";
//    String emergencyType = "general";

    private static final int RC_SIGN_IN = 1;

    public static String organisation;

    static String mLat,mLon;

    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

//    private FusedLocationProviderClient fusedLocationClient;
    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String mUsername;
    public static String mUid,mToken;
//    String lat, lon;
    private static int REQUEST_PHONE_CALL=1;
//    Button helpButton;
//    Button generalButton,medicalButton,fireButton,disasterButton;

//    @SuppressLint("MissingPermission")
//    void makeCall(String type) {
//        fusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                Log.d(TAG, "onSuccess: got location");
//
//                if (location != null) {
//                    // Logic to handle location
//                    lat = location.getLatitude() + "";
//                    lon = location.getLongitude() + "";
//                    Log.d(TAG, "onSuccess: Location found");
//                    Log.d(TAG, "onSuccess: Lat is "+lat+"Long is "+lon);
//                }
//            }
//        });
//        String url = getResources().getString(R.string.server) + "requests";
//        RequestBody body = new FormBody.Builder()
//                .add("uid", mUid)
//                .add("loc", lat + " " + lon)
//                .add("type", type)
//                .add("msg","")
//                .build();
//
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        Toast.makeText(getApplicationContext(), "Trying to Login", Toast.LENGTH_LONG).show();
//
//        client.newCall(request).enqueue(new Callback(){
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
//                runOnUiThread(new Runnable(){
//                    @Override
//                    public void run() {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                e.printStackTrace();
//            }
//        });
//        String phoneNumber;
//        if(type.equalsIgnoreCase("general"))
//        {
//            phoneNumber=getString(R.string.general_call);
//        }
//        else if(type.equalsIgnoreCase("fire")){
//            phoneNumber=getString(R.string.fire_call);
//        }
//        else if (type.equalsIgnoreCase("medical")){
//            phoneNumber=getString(R.string.medical_call);
//        }
//        else {
//            phoneNumber=getString(R.string.disaster_call);
//        }
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
//        startActivity(intent);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
//                                    Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                                }
                            });

                    chirp = new ChirpSDK(getApplicationContext(), CHIRP_APP_KEY, CHIRP_APP_SECRET);
                    ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
                    if (error.getCode() == 0) {
                        Log.v("ChirpSDK: ", "Configured ChirpSDK");
                    } else {
                        Log.e("ChirpError: ", error.getMessage());
                    }

                    startService(new Intent(getApplicationContext(), Listener.class));

                    //now checking network connectivity.
                    if(isNetworkAvailable()){
                        Log.d(TAG, "onActivityResult: NETOWRK AVAILABLE");
                        Intent intent= new Intent(MainActivity.this,com.bitsplease.rakshak.InNetwork.class);
                        startActivity(intent);
                    }

                    else if(!isNetworkAvailable()){
                        Log.d(TAG, "onActivityResult: NETOWRK AVAILABLE");
                        Intent intent= new Intent(MainActivity.this,com.bitsplease.rakshak.NoNetwork.class);
                        startActivity(intent);
                    }

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

    void sendDataToFirebase(String uid,String token) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                if (!hasPermissions(this, getRequiredPermissions())) {
                    Log.d("Hello", "There");
                    requestPermissions(getRequiredPermissions(), REQUEST_CODE_REQUIRED_PERMISSIONS);
                }

                chirp = new ChirpSDK(this, CHIRP_APP_KEY, CHIRP_APP_SECRET);
                ChirpError error = chirp.setConfig(CHIRP_APP_CONFIG);
                if (error.getCode() == 0) {
                    Log.v("ChirpSDK: ", "Configured ChirpSDK");
                } else {
                    Log.e("ChirpError: ", error.getMessage());
                }

                startService(new Intent(getApplicationContext(), Listener.class));

                //now checking network connectivity.
                if(isNetworkAvailable()){
                    Log.d(TAG, "onActivityResult: NETOWRK AVAILABLE");
                    Intent intent= new Intent(MainActivity.this,com.bitsplease.rakshak.InNetwork.class);
                    startActivity(intent);
                }

                else if(!isNetworkAvailable()){
                    Log.d(TAG, "onActivityResult: NETOWRK AVAILABLE");
                    Intent intent= new Intent(MainActivity.this,com.bitsplease.rakshak.NoNetwork.class);
                    startActivity(intent);
                }


            }

            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(this, "Cannot work, until you sign in.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    protected String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
