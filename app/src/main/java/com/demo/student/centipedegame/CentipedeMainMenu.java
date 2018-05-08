package com.demo.student.centipedegame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CentipedeMainMenu extends AppCompatActivity {
    Button mHighScoreButton;
    Button mStartGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centipede_main_menu);

        mStartGameButton = (Button)findViewById(R.id.startGameButton);
        mHighScoreButton = (Button)findViewById(R.id.highScoreButton);

        mStartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mHighScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PostgameScreen.class);
                intent.putExtra("KEY_IS_FROM_MAIN", true);
                startActivity(intent);
                finish();
            }
        });
    }

}
