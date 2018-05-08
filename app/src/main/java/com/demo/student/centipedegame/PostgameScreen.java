package com.demo.student.centipedegame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class PostgameScreen extends AppCompatActivity {

    AlertDialog submitScoreDialog;
    RecyclerView recycleView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<PlayerScoreInfo> arrayList = new ArrayList<>();
    Button mButtonMenu;
    Button mButtonSubmitScore;
    private boolean isFromMainMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postgame_screen);
        Bundle extras = getIntent().getExtras();
        final TextView playerScoreNumber  = findViewById(R.id.playerScoreNumber);
        if(extras != null){
            String value = Integer.toString(extras.getInt("PLAYER_SCORE"));
            playerScoreNumber.setText(value);
        }

        recycleView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setHasFixedSize(true);

        final BackgroundTask backgroundTask = new BackgroundTask(PostgameScreen.this);
        mButtonMenu = (Button) findViewById(R.id.startGameButton);
        mButtonSubmitScore = (Button) findViewById(R.id.submitScoreButton);

        mButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(PostgameScreen.this, CentipedeMainMenu.class);
                startActivity(myIntent);
            }
        });
        if(getIntent().hasExtra("KEY_IS_FROM_MAIN")) {
            isFromMainMenu = getIntent().getBooleanExtra("KEY_IS_FROM_MAIN", false);
            if (!isFromMainMenu) {
                mButtonSubmitScore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PostgameScreen.this);
                        View mView = getLayoutInflater().inflate(R.layout.submit_score_dialog,null);
                        final EditText mPlayerName = (EditText) mView.findViewById(R.id.playerScoreName);
                        Button mCancelDialog = (Button) mView.findViewById(R.id.cancelDialog);
                        Button mAcceptDialog = (Button) mView.findViewById(R.id.acceptDialog);

                        mCancelDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitScoreDialog.dismiss();
                            }

                        });

                        mAcceptDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitScoreToBackend(backgroundTask,mPlayerName.getText().toString(), playerScoreNumber.getText().toString());
                                submitScoreDialog.dismiss();
                            }

                        });
                        mBuilder.setView(mView);
                        submitScoreDialog = mBuilder.create();
                        submitScoreDialog.show();

                    }
                });
            } else {
                mButtonSubmitScore.setVisibility(View.GONE);
            }
        }
        displayHighScoreList(backgroundTask);




    }

    public void displayHighScoreList(BackgroundTask backgroundTask){
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



    public void submitScoreToBackend(BackgroundTask backgroundTask, String playerName, String playerScore){
        backgroundTask.submitHighScore(playerName, playerScore, new VolleyCallbackListener() {
            @Override
            public void onResponseCallback(ArrayList<PlayerScoreInfo> arrayList) {
                //mButtonSubmitScore.setVisibility(View.GONE);
                //mButtonSubmitScore.setVisibility(View.INVISIBLE);
                mButtonSubmitScore.setEnabled(false);
            }

            @Override
            public void onErrorCallback() {

            }
        });
    }
}
