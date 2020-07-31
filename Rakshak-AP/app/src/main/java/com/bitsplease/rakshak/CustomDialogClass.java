package com.bitsplease.rakshak;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.nio.charset.Charset;

import io.chirp.chirpsdk.models.ChirpError;

import static com.bitsplease.rakshak.NoNetwork.chirp;
import static com.bitsplease.rakshak.NoNetwork.mLat;
import static com.bitsplease.rakshak.NoNetwork.mLon;


public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button go;
    String TAG = "MyLOGS";
    public Spinner spinnerdialogue;
    String value="General Emergency";
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;
    public static String CHIRP_APP_KEY = "a425Bff1fE83BaFD9e5F6d5A3";
    public static String CHIRP_APP_SECRET = "A0DFeB41c736196Fa82fBFCcAa36E334610Ea71bd128e7e9D5";
    public static String CHIRP_APP_CONFIG = "GpEu5S5FtjVzSmhBNpDrmLU9Ojw4c2xcsrhoPuerCsqSNfoicEuPCx0iX1lNFMYN2ekd4+HAT1wXVvvgqaTrm7FXO3EKt6aDCEOYcZtc8oPSMNk83Q/UMFfdgimH5AYSbx9yFiuvoKAuhXA31VsiEfdYSLD82zXTgEjgTYyzje1BMRIEqXKV3fG6pT14vftbJ1gc3qJR1RW4+/g1bVqKo6zG7gHkC+qwzSrqQ1/63lA2wMQ8Cvu3mmMzvgFVWlsBUg++sxGaztNCX0F7Ig96oi7PGeVGZGj5nnicfJsL3RHH2siNwoILh9E6SkejXNGq5uq35juxz1ySslDGTOr2y0yvKxjfgC5JI2+01TLlXGPTY8q7cDpASP9rbSwHWoEu7HIxHgu/g1ZZTfo21HxAkjHcxg0Zj+25HkTCalQ/jbrB33yYEUUI+05l+dP0OU29SMeZ1G2xTmrzy2nerEzTOW9CECAu/X0Vy6Wk+qYScuW64uboqeQnSfer5qmDK44jNYuwAg9ZklpzkTKaIRD/2bpsBElAwwS5UvTI5u2uQ/obYopGHC6VB88Ird1Q41FGGnIfMYwmvRJfPpBa4TGvU8S/9NoNZF891m0FYvy0FoN2kxus+Xi6z7O2lvcEGON+aiiKenCC+xdAimpNEGVJyQH36AG6KsIz3iAJT+Q/lpBwbWerYf5s7SlNZhlkxeWQfb+X7p/SSNndfrxsuX5g3RhMKXbXvG3bQueQHzguaWi5ykqMWzioEQA/xGeRhPu1gYaMDXYmonLfX/WWEuEpEqhmSlNh/ePnqM85CMkXBPz0AbleD5Xe0nz+f/hO2iX09br1ymNpJ2PnhEwpAPffoGNNYrxceUxCYYEMImRMaBjskKxoGl5jxknvY9G5jn6kW16/91NApoeul75yRFdzqF5fYT68uxNTzlF9stX6ukvsDoWPr8EFWl0OY82pgUfRiGdQwyy/0f9k8SKD41Ptcyac0VVeiZcoLeuRtm3EJPk9bgMk7FpQmkTz/dOSXVCb4upARtDmGY1+v5nbsxNEocmC9aVlHWXyYKqzKjZzf15XPLMUGP5/HPLA+8bNs7G9MT+vWi5EhR/QMKaDykLPdQCKrDJvXdPxeKVriDIqRGim24EmheFz4lb2Q2apYkc4JCD4gnXKuvrTdhtBtHZx3A==";



    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialogue);
        go=(Button)findViewById(R.id.BTNgo);
        spinnerdialogue=findViewById(R.id.spinner2);
        String[] spinnerEmergencylist = {"General Emergency", "Medical", "Fire","Disaster"};
        ArrayAdapter<String> spinnerEmergencyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, spinnerEmergencylist);
        spinnerEmergencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdialogue.setAdapter(spinnerEmergencyAdapter);
        go.setOnClickListener(this);
        spinnerdialogue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("General Emergency")){
//                    Toast.makeText(InNetwork.this, "General Emergency selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, "onItemSelected: in else");
                    Toast.makeText(c, parent.getItemAtPosition(position).toString()+" selected.", Toast.LENGTH_SHORT).show();
                    value=parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: nothing selected");
                Toast.makeText(c, "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BTNgo:
                Toast.makeText(c, "Go clicked", Toast.LENGTH_SHORT).show();
                makeNoNetCall(value);
                break;
            default:
                break;
        }
        dismiss();
    }

    void makeNoNetCall(String Emergency){
        String identifier = Emergency.charAt(0)+mLat.substring(6,9)+mLon.substring(6,9);
        Log.d(TAG, "makeNoNetCall: Indentifier is "+identifier);
        byte[] payload = identifier.getBytes(Charset.forName("UTF-8"));
        ChirpError error = chirp.send(payload);
        if (error.getCode() > 0) {
            Log.e("ChirpError: ", error.getMessage());
        } else {
            Log.v("ChirpSDK: ", "Sent " + identifier);
        }
    }
}