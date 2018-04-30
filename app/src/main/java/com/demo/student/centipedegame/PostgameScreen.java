package com.demo.student.centipedegame;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class PostgameScreen extends AppCompatActivity {
    RecyclerView recycleView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<PlayerScoreInfo> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postgame_screen);
        Bundle extras = getIntent().getExtras();
        TextView playerScoreNumber  = findViewById(R.id.playerScoreNumber);
        if(extras != null){
            String value = Integer.toString(extras.getInt("PLAYER_SCORE"));
            playerScoreNumber.setText(value);
        }

        recycleView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setHasFixedSize(true);

        BackgroundTask backgroundTask = new BackgroundTask(PostgameScreen.this);

        backgroundTask.getList(new VolleyCallbackListener(){
            @Override
            public void onResponseCallback(ArrayList<PlayerScoreInfo> arrayList){
                adapter =  new HighScoreAdapter(arrayList);
                recycleView.setAdapter(adapter);
            }

            @Override
            public void onErrorCallback() {

            }
        });



    }


    public void thisIsATest(){
        System.out.println("THIS IS A TEST");
    }
}
