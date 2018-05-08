package com.demo.student.centipedegame;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    String json_url = "http://24.5.146.109:3000/api/highScore"; // "http://192.168.2.9:3000/api/highScore";

    String json_url2 = "http://192.168.2.10:3000/api/highScore?";

    String brute_json_url = "http://192.168.2.10:3000/api/highScore?playerScore=4567&playerName=WhyAreYouLikeThis";

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

    public void submitHighScore(final String playerName, final String playerScore, final VolleyCallbackListener listener){
        StringRequest postRequest = new StringRequest(Request.Method.POST,json_url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(context, response , Toast.LENGTH_LONG).show();
                        listener.onResponseCallback(arrayList);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(context, "Error: unable to submit Score " + error , Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("playerName", playerName);
                params.put("playerScore",playerScore);
                return params;
            }
        };

        HighScoreSingleton.getInstance(context).addToRequestQueue(postRequest);

    }

}
