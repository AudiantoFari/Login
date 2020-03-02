package com.ian.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import de.hdodenhof.circleimageview.CircleImageView;

//referensi https://dedykuncoro.com/2017/03/tutorial-membuat-aplikasi-android-login-dan-register-database-mysql.html

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogout;
    TextView tvNama;
    String nama, username;
    SharedPreferences sharedpreferences;
    CircleImageView civPhoto;

    public static final String TAG_NAMA = "nama";
    public static final String TAG_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.btnLogout);
        tvNama = findViewById(R.id.tvNama);
        civPhoto = findViewById(R.id.civPhoto);

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        nama = getIntent().getStringExtra(TAG_NAMA);
        username = getIntent().getStringExtra(TAG_USERNAME);

        tvNama.setText(username);

        Glide.with(this)
                .load(R.drawable.doge)
                .apply(new RequestOptions())
                .into(civPhoto);

        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_NAMA, null);
                editor.commit();

                Intent intent = new Intent(this, Login.class);
                finish();
                startActivity(intent);
        }
    }
}
