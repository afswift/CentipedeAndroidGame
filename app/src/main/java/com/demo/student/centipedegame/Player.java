package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by butle on 3/18/2018.
 */

public class Player extends GameObject {
    private Bitmap spritesheet;
    private int score;
    private int xPrev;
    private int yPrev;

    public Player(Bitmap res){
        x=GamePanel.WIDTH / 2;
        y=504;
        dy = 0;
        score = 0;
        height = 16;
        width = 14;
        spritesheet = Bitmap.createScaledBitmap(res, 14, 16, false);
    }

    public Player(Bitmap res, int x, int y){
        x=GamePanel.WIDTH / 2;
        y=504;
        dy = 0;
        score = 0;
        height = 16;
        width = 14;
        spritesheet = Bitmap.createScaledBitmap(res, 14, 16, false);
    }

    public void setDirection(float Angle, float Magnitude){
        dx = (int) (10 * Magnitude * Math.cos(Angle));
        dy = (int) (10 * Magnitude * Math.sin(Angle));
    }

    public void setNeutral(){
        dy = 0;
        dx = 0;
    }

    public void update(){
        xPrev = x;
        yPrev = y;
        if( x + dx < 0)
            x = 0;
        else if(x + dx > 480 - width)
            x = 480 - width;
        else
            x += dx;

        if(y + dy > 514 - height)
            y = 514 - height;
        else if(y +  dy < 448)
            y = 448;
        else
            y += dy;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(spritesheet, x, y, null);
    }

    public void solidObjectCollision(GameObject solidObject){


        // from right
        if(xPrev >= (solidObject.getX()+ solidObject.getWidth())){
            x = solidObject.getX() + solidObject.getWidth();//+1);
            dx=0;
        }
        // from left
        else if(xPrev + width <= solidObject.getX()){
            x = solidObject.getX() - width; // - 1);
            dx=0;
        }
        else if((yPrev + height) <= solidObject.getY()){
            y=solidObject.getY()- height; //-1);
            dy=0;
        }
        // from right
        else if(yPrev >= (solidObject.getY()+ solidObject.getHeight())){
            y=solidObject.getY() + solidObject.getHeight();//+1);
            dy=0;
        }
    }
    public int getScore(){
        return score;
    }

    public void resetDY(){
        dy= 0;
    }

    public void resetScore(){
        score = 0;
    }

    public int getxPrev() {
        return xPrev;
    }

    public void setxPrev(int xPrev) {
        this.xPrev = xPrev;
    }

    public int getyPrev() {
        return yPrev;
    }

    public void setyPrev(int yPrev) {
        this.yPrev = yPrev;
    }

    public Rect getProjectedRectangle(){
        return new Rect(x+ dx,y + dy, x+width, y+height);
    }
}

