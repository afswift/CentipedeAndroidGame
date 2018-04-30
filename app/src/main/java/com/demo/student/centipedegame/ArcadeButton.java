package com.demo.student.centipedegame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by butle on 3/26/2018.
 */

public class ArcadeButton {
    private float centerX;
    private float centerY;
    private float radius;
    private boolean buttonPressed;
    private boolean ignoreAction;
    private Paint buttonBaseColor;
    private Paint buttonPressedColor;
    private int buttonPointerId = -1;

    public ArcadeButton(int centerX, int centerY, int radius, Paint color1, Paint color2){
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.buttonBaseColor = color1;
        this.buttonPressedColor = color2;
        ignoreAction = false;
    }

    public void onMotionEvent(MotionEvent event, float widthScale, float heightScale) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                if (insideJoystickBounds(event.getX(event.getActionIndex()) / widthScale, event.getY(event.getActionIndex()) / heightScale)) {
                    buttonPressed=true;
                    ignoreAction = false;
                    return;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (insideJoystickBounds(event.getX(event.getActionIndex()) / widthScale, event.getY(event.getActionIndex()) / heightScale)) {
                    buttonPressed=false;
                    ignoreAction = false;
                    return;
                }
                break;


        }
        if(!ignoreAction) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                if (insideJoystickBounds(event.getX(i) / widthScale, event.getY(i) / heightScale)) {
                    buttonPointerId = i;
                    buttonPressed = true;
                    ignoreAction = false;
                    return;
                }
            }
        }
        ignoreAction=true; //
        buttonPressed = false;
    }

    public boolean isButtonPressed(){
        return buttonPressed;
    }


    public boolean insideJoystickBounds(float coordX, float coordY){
        if(currentRadius(coordX, coordY)<radius)
            return true;
        return false;
    }

    public float currentRadius(float coordX, float coordY){
        float pythagX = coordX - centerX;
        float pythagY = coordY - centerY;
        return (float) Math.sqrt((pythagX * pythagX) + (pythagY * pythagY));
    }

    public void draw(Canvas canvas){
        if(!buttonPressed)
            canvas.drawCircle(centerX, centerY , radius, buttonBaseColor);
        else
            canvas.drawCircle(centerX, centerY , (int) (radius* 1.1), buttonPressedColor);
    }
}
