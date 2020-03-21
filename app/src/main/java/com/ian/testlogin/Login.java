package com.ian.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    EditText txtID, txtPassword;
    Button btnLogin;
    TextView tvRegister;
    ProgressDialog pDialog;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "proses_login.php";
    private static final String TAG = Login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id";
    public final static String TAG_GAMBAR = "gambar";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    boolean session = false;
    String id, nama, gambar;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        txtID = findViewById(R.id.txtID);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        id = sharedPreferences.getString(TAG_ID, null);
        nama = sharedPreferences.getString(TAG_USERNAME, null);
        gambar = sharedPreferences.getString(TAG_GAMBAR, null);

        if (session) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra(TAG_USERNAME, nama);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_GAMBAR, gambar);
            finish();
            startActivity(intent);
        }

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin :
                String username = txtID.getText().toString();
                String password = txtPassword.getText().toString();

                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tvRegister :
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                break;
        }
    }


    private void checkLogin(final String username, final String password) {
        pDialog = new ProgressDialog(Login.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response : " + response);
                pDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        String username = jObj.getString(TAG_ID);
                        String nama = jObj.getString(TAG_USERNAME);
                        String gambar = jObj.getString(TAG_GAMBAR);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, username);
                        editor.putString(TAG_USERNAME, nama);
                        editor.putString(TAG_GAMBAR, gambar);
                        editor.commit();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra(TAG_USERNAME, nama);
                        intent.putExtra(TAG_ID, username);
                        intent.putExtra(TAG_GAMBAR, gambar);
                        startActivity(intent);
                        finish();
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
                Log.e(TAG, "Login Error : " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        AppController.getmInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
