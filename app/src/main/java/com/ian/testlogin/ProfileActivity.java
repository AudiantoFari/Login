package com.ian.testlogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnUpload, btnSubmit;
    SharedPreferences sharedpreferences;
    EditText txtNama, txtPassword, txtNewPass, txtConfirmNew;
    CircleImageView civEdit;
    ProgressDialog pDialog;

    String encodeImage;

    String nama;
    String password;
    String newpassword;
    String confirmpass;
    String username;

    public Bitmap getGambar() {
        return gambar;
    }

    public void setGambar(Bitmap gambar) {
        this.gambar = gambar;
    }

    Bitmap gambar;

    public static final String TAG_USERNAME = "username";
    public static final String TAG_GAMBAR = "gambar";

    private static final String url = Server.URL + "proses_edit.php";

    int success;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnUpload = findViewById(R.id.btnUpload);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtNama = findViewById(R.id.txtNama);
        txtPassword = findViewById(R.id.txtPassword);
        txtNewPass = findViewById(R.id.txtNewPass);
        civEdit = findViewById(R.id.civEdit);

        loadGambar();

        btnUpload.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void loadGambar() {
        Glide.with(this)
                .load(Server.URL + "img/" + getIntent().getStringExtra(TAG_GAMBAR)+".png")
                .apply(new RequestOptions())
                .error(R.drawable.doge)
                .into(civEdit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpload:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_IMG);
                break;
            case R.id.btnSubmit:
                nama = txtNama.getText().toString();
                password = txtPassword.getText().toString();
                newpassword = txtNewPass.getText().toString();
                username = getIntent().getStringExtra(TAG_USERNAME);

                updateProfile(nama, password, newpassword, bitmapToString(getGambar()), username);
                break;
        }
    }

    private String bitmapToString(Bitmap gambar) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            gambar.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] imageBytes = baos.toByteArray();
            encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            encodeImage = "";
        }
        return encodeImage;
    }

    private void updateProfile(final String nama, final String password, final String newpassword, final String gambar, final String username) {
        pDialog = new ProgressDialog(ProfileActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Process...");
        pDialog.show();



        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Update Response", response);
                pDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Update", jObj.toString());

                        Toast.makeText(ProfileActivity.this, "Update berhasil, Silahkan login kembali", Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        Log.e("Update", jObj.toString());
                        Toast.makeText(ProfileActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Update", "Error : " + error.getMessage());
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("username", username);
                params.put("password", password);
                params.put("newpassword", newpassword);
                params.put("gambar", gambar);
                return params;
            }
        };
        AppController.getmInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Glide.with(this)
                        .load(selectedImage)
                        .apply(new RequestOptions())
                        .into(civEdit);
                setGambar(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal upload gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
