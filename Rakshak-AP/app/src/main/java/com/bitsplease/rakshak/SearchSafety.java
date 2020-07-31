package com.bitsplease.rakshak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SearchSafety extends AppCompatActivity {

    Animation animRotation;
    Animation animFadein;
    Button rotateButton;
    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_safety);

        rotateButton=findViewById(R.id.BTNrotate);
        txtView= findViewById(R.id.perText);

        animRotation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Toast.makeText(SearchSafety.this,"Started Rotation",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Toast.makeText(SearchSafety.this,"End Rotation",Toast.LENGTH_SHORT).show();
                txtView.setVisibility(View.VISIBLE);
                animFadein.restrictDuration(5000);
                txtView.startAnimation(animFadein);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Toast.makeText(SearchSafety.this,"Repeat Rotation",Toast.LENGTH_SHORT).show();
            }
        };
        animRotation.setAnimationListener(animationListener);
        animRotation.restrictDuration(3000);
        rotateButton.startAnimation(animRotation);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animRotation.start();
                rotateButton.startAnimation(animRotation);
            }
        });

    }




}