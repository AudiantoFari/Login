package com.ian.testlogin;

//referensi https://badoystudio.com/membuat-splash-screen-dengan-mudah-di-android-studio/

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class Splash extends AppCompatActivity {

    CircleImageView civPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        civPhoto = findViewById(R.id.civPhoto);

        Glide.with(this)
                .load(R.drawable.ian)
                .apply(new RequestOptions())
                .into(civPhoto);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }
}
