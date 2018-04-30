package com.demo.student.centipedegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by butle on 3/31/2018.
 */

public class Laser extends GameObject {
    Paint paint;

    Laser(){
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        dy = -15;
        width = 2;
        height = 10;
    }


    public void update(){
        if(y < 1000 || y > -15)
            y+=dy;
    }

    public void draw(Canvas canvas){
        if(y < 1000 &&  y > -15)
        canvas.drawLine(x,y, x, (y+height), paint);
    }

    public void resetLaser(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean isLaserOffscreen(){
        if(y > 1000)
            return true;
        return false;

    }

    public void stopFiring(){
        this.x = -15;
        this.y = - 15;
        this.dy = - 15;
    }

    public boolean isLaserOnscreen(){
        if(y > 0 && y < 512)
            return true;
        return false;
    }

}
