package com.demo.student.centipedegame;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

interface VolleyResponseListener {
    void onError(String message);

    void onResponse(Object response);
}

public class MainActivity extends Activity {

    GamePanel newGamePanel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newGamePanel = new GamePanel(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(newGamePanel);

        newGamePanel.addGamePanelBooleanListener(new GamePanelBooleanChangedListener() {
            @Override
            public void OnMyBooleanChanged() {
                System.out.println("GameOver Achieved");
                Intent intent = new Intent(getBaseContext(), PostgameScreen.class);
                intent.putExtra("PLAYER_SCORE", newGamePanel.getScore());
                intent.putExtra("KEY_IS_FROM_MAIN", false);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
// call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
        outState.putIntArray("centipedeMovement", newGamePanel.saveCentipedePosition());
        outState.putBooleanArray("centipedeBooleans", newGamePanel.saveCentipedeBoolean());
        outState.putIntArray("playerPosition", newGamePanel.savePlayerPosition());
        outState.putIntArray("mushroomsPosition", newGamePanel.saveMushroomsPosition());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState){
        super.onRestoreInstanceState(savedState);
        if(savedState!=null) {
            newGamePanel.reloadPlayerPosition(savedState.getIntArray("playerPosition"));
            newGamePanel.reloadCentipede(savedState);
            newGamePanel.reloadMushrooms(savedState.getIntArray("mushroomsPosition"));
            newGamePanel.setIsPlaying(false);
        }
    }

    @Override
    public void onPause(){
        System.out.println("On Pause was called");
        super.onPause();
    }

    @Override
    public void onStop(){
        System.out.println("On Stop was called");
        super.onStop();
    }
}
