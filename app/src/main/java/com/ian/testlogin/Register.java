package com.ian.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText txtID, txtPassword, txtConfirm, txtNama;
    Button btnRegister;
    ProgressDialog pDialog;

    String nama, username, password, confirm;

    private static final String url = Server.URL + "proses_register.php";

    int success;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtID = findViewById(R.id.txtID);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirm = findViewById(R.id.txtConfirmPassword);
        txtNama = findViewById(R.id.txtUsername);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister :
                nama = txtNama.getText().toString();
                username = txtID.getText().toString();
                password = txtPassword.getText().toString();
                confirm = txtConfirm.getText().toString();

                if (nama.isEmpty()) {
                    txtNama.setError("Field ini tidak boleh kosong");
                    txtNama.requestFocus();
                } else if (username.isEmpty()) {
                    txtID.setError("Field ini tidak boleh kosong");
                    txtID.requestFocus();
                } else if (password.isEmpty()) {
                    txtPassword.setError("Field ini tidak boleh kosong");
                    txtPassword.requestFocus();
                } else if (confirm.isEmpty()) {
                    txtConfirm.setError("Field ini tidak boleh kosong");
                    txtConfirm.requestFocus();
                } else if (confirm != null && !confirm.equals(password)) {
                    txtConfirm.setError("Password tidak sama");
                    txtConfirm.requestFocus();
                } else {
                    register(nama, username, password);
                }
                break;
        }
    }

    private void register(final String nama, final String username, final String password) {
        pDialog = new ProgressDialog(Register.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Registering...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Register Response", response);
                pDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Register", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
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
                Log.e("Register", "Error : "+ error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map <String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        AppController.getmInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
