package com.demo.student.centipedegame;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    GamePanel newGamePanel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        newGamePanel = new GamePanel(this);
        // removes title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(newGamePanel);
        System.out.println("Was this called again?");
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
        System.out.println("On SavedInstance State was called");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedState){
        super.onRestoreInstanceState(savedState);

        if(savedState!=null) {
            newGamePanel.reloadPlayerPosition(savedState.getIntArray("playerPosition"));
            newGamePanel.reloadCentipede(savedState);
            newGamePanel.reloadMushrooms(savedState.getIntArray("mushroomsPosition"));
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
