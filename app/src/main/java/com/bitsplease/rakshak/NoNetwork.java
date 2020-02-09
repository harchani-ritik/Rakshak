package com.bitsplease.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoNetwork extends AppCompatActivity {

    Button BTNemergencyMSG,BTNwalkietalkie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);

        BTNemergencyMSG=findViewById(R.id.BTNemergencyMSG);
        BTNwalkietalkie=findViewById(R.id.BTNwalkietalkie);
    }

    public void handleWalkieTalkie(View view){
        Intent intent= new Intent(NoNetwork.this,com.bitsplease.rakshak.WalkiTalkie.class);
        startActivity(intent);
    }
}