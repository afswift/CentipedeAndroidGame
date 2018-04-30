package com.demo.student.centipedegame;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by butle on 4/28/2018.
 */

interface VolleyCallbackListener{ // used to make callback to activity
    void onResponseCallback(ArrayList<PlayerScoreInfo> arrayList);
    void onErrorCallback();
}

public class BackgroundTask {
    Context context;
    ArrayList<PlayerScoreInfo> arrayList = new ArrayList<>();
    String json_url = "http://192.168.2.10:3000/api/highScore";

    public BackgroundTask(Context context){
        this.context = context;
    }

    public void getList(final VolleyCallbackListener listener){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, json_url, null,
            new Response.Listener<JSONArray>(){
                @Override
                public void onResponse(JSONArray response){
                    for(int i = 0; i < response.length(); i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            PlayerScoreInfo playerInfo = new PlayerScoreInfo(jsonObject.getString("playerName"), jsonObject.getLong("playerScore"));
                            arrayList.add(playerInfo);
                            System.out.println(jsonObject.getString("playerName"));

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    listener.onResponseCallback(arrayList);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Toast.makeText(context, "Error: unable to get Score " + error , Toast.LENGTH_SHORT).show();
                }
        }
        );

        HighScoreSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);

    }

}
