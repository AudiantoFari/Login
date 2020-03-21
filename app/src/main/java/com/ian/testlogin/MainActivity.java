package com.ian.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//referensi https://dedykuncoro.com/2017/03/tutorial-membuat-aplikasi-android-login-dan-register-database-mysql.html

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogout, btnAbsen, btnEdit;
    TextView tvNama;
    String nama, username, gambar;
    SharedPreferences sharedpreferences;
    CircleImageView civPhoto;
    ProgressDialog pDialog;

    public static final String TAG_USERNAME = "username";
    public static final String TAG_ID = "id";
    public static final String TAG_GAMBAR = "gambar";

    private static final String url = Server.URL + "proses_absen.php";

    int success;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.btnLogout);
        tvNama = findViewById(R.id.tvNama);
        civPhoto = findViewById(R.id.civPhoto);
        btnAbsen = findViewById(R.id.btnAbsen);
        btnEdit = findViewById(R.id.btnEdit);

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        nama = getIntent().getStringExtra(TAG_USERNAME);
        username = getIntent().getStringExtra(TAG_ID);
        gambar = getIntent().getStringExtra(TAG_GAMBAR);

        tvNama.setText(nama);

        loadGambar();

        btnLogout.setOnClickListener(this);
        btnAbsen.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void loadGambar() {
        Glide.with(this)
                .load(Server.URL + "img/" + gambar+".png")
                .apply(new RequestOptions())
                .error(R.drawable.doge)
                .into(civPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:
                logout();
                break;
            case R.id.btnEdit :
                Intent intent1 = new Intent(this, ProfileActivity.class);
                intent1.putExtra(ProfileActivity.TAG_USERNAME, username);
                intent1.putExtra(ProfileActivity.TAG_GAMBAR, gambar);
                startActivity(intent1);
                break;
            case R.id.btnAbsen :
                absen(username);
                break;
        }
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(Login.session_status, false);
        editor.putString(TAG_ID, null);
        editor.putString(TAG_USERNAME, null);
        editor.commit();

        Intent intent = new Intent(this, Login.class);
        finish();
        startActivity(intent);
    }

    private void absen(final String username) {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Sedang mengabsen...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Login Response", response);
                pDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Absen", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Absen", "Error : " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };
        AppController.getmInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
