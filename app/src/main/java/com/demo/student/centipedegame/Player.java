package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by butle on 3/18/2018.
 */

public class Player extends GameObject {
    private Bitmap spritesheet;
    private int score;
    private boolean playing;
    private boolean up;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames){
        x=100;
        y=(int)GamePanel.HEIGHT/2;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for(int i = 0; i<image.length; i++){
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setDirection(float Angle){

        dx = (int) (10* Math.cos(Angle));
        dy = (int) (10* Math.sin(Angle));
    }

    public void setNeutral(){
        dy = 0;
        dx = 0;
    }

    public void update(){
        long elapsed = (System.nanoTime() - startTime)/1000000;
        if(elapsed>100){
            score++;
            startTime = System.nanoTime();
        }

        animation.update();
        x += dx;
        y += dy;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(animation.getImage(),x,y, null);
    }

    public int getScore(){
        return score;
    }

    public boolean getPlaying(){
        return playing;
    }

    public void setPlaying(boolean b){
        playing = b;
    }

    public void resetDY(){
        dy= 0;
    }

    public void resetScore(){
        score = 0;
    }
}

