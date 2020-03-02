package com.ian.testlogin;

import android.app.Application;
import android.content.Context;
import android.nfc.Tag;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.lang.ref.ReferenceQueue;

import static com.android.volley.VolleyLog.TAG;

public class AppController extends Application {

    public static final String tag = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getmInstance() {
        return mInstance;
    }

    public RequestQueue getmRequestQueue() {
        if  (mRequestQueue == null) {
            Context context;
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ?  TAG : tag);
        getmRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getmRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if  (mRequestQueue !=null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
