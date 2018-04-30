package com.demo.student.centipedegame;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by butle on 4/29/2018.
 */



public class HighScoreSingleton {
    private static HighScoreSingleton mInstance;
    private RequestQueue requestQueue;
    private Context mContext;

    private HighScoreSingleton(Context context){
        mContext = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized HighScoreSingleton getInstance(Context context){
        if(mInstance==null){
            mInstance = new HighScoreSingleton(context);
        }
        return mInstance;
    }

    public<T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }
}
