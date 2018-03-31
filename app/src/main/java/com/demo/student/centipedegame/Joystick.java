package com.demo.student.centipedegame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

/**
 * Created by butle on 3/25/2018.
 */

public class Joystick {
    private float centerX;
    private float centerY;
    private float currentX;
    private float currentY;
    private float radius;
    private Paint joystickBaseColor;
    private Paint joystickSecondaryColor;
    private boolean ignoreAction;
    private int joystickPointerId = -1;

    public Joystick(int centerX, int centerY, int radius, Paint color1, Paint color2){
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.joystickBaseColor = color1;
        this.joystickSecondaryColor = color2;
        this.currentX = centerX;
        this.currentY = centerY;
        ignoreAction = false;
}

    public void onMotionEvent(MotionEvent event, float widthScale, float heightScale){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                for(int i = 0; i< event.getPointerCount(); i++) {
                    if (insideJoystickBounds(event.getX(i) / widthScale, event.getY(i) / heightScale)) {
                        currentX = event.getX(i) / widthScale;
                        currentY = event.getY(i) / heightScale;
                        System.out.println(getJoystickAngle());
                        ignoreAction = false;
                        return;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                float tempRatio = radius / currentRadius(event.getX(event.getActionIndex()) / widthScale, event.getY(event.getActionIndex()) / heightScale);
                if(tempRatio > 0.50) {
                    currentX = centerX;
                    currentY = centerY;
                    ignoreAction = false;
                    return;
                }
                break;

        }
        if(!ignoreAction) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                if (insideJoystickBounds(event.getX(i) / widthScale, event.getY(i) / heightScale)) {
                    currentX = event.getX(i) / widthScale;
                    currentY = event.getY(i) / heightScale;
                    return;
                } else {
                    float tempRatio = radius / currentRadius(event.getX(i) / widthScale, event.getY(i) / heightScale);
                    if (tempRatio > 0.50) {
                        currentX = centerX + (event.getX(i) / widthScale - centerX) * tempRatio;
                        currentY = centerY + (event.getY(i) / heightScale - centerY) * tempRatio;
                        return;
                    }
                }
            }
        }

        ignoreAction = true;
        currentX = centerX;
        currentY = centerY;
    }

    public boolean insideJoystickBounds(float coordX, float coordY){
        if(currentRadius(coordX, coordY)<radius) {
            return true;
        }
        return false;
    }

    public float currentRadius(float coordX, float coordY){
        float xPythag = coordX - centerX;
        float yPythag = coordY - centerY;

        return (float) Math.sqrt((xPythag * xPythag) + (yPythag * yPythag));
    }

    public float getJoystickAngle(){
        return (float)(Math.atan2(currentY-centerY, currentX-centerX));
    }

    public boolean isIdle(){
        if(currentX == centerX && currentY == centerY)
            return true;
        return false;
    }


    public void draw(Canvas canvas){
        canvas.drawCircle(centerX, centerY , radius, joystickBaseColor);
        canvas.drawCircle(currentX, currentY , (int) (radius * 0.8), joystickSecondaryColor);

    }
}
