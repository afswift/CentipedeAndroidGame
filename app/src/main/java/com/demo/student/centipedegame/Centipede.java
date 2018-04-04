package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by butle on 4/1/2018.
 */

public class Centipede extends GameObject {
    private Animation animation = new Animation();
    private long startTime;
    private boolean head;
    private Bitmap spritesheet;
    private int frameCount;
    Bitmap[][] image;
    private int currentFrame;
    private int directionCode = 0; // TODO: ADD Better name for code
    private int prevDirectionCode = 0;
    private int prev2DirectionCode = 0;
    private int prevX = 0;
    private int prevY = 0;
    private int prev2X;
    private int prev2Y;

    // directionCode 0 is left, 1 is right, 2 is diag, 3 is down

    public Centipede(Bitmap res, boolean isHead, int x, int y){
        this.head = isHead;
        this.width = 16;
        this.height = 16;
        this.x = x;
        this.y = y;
        this.directionCode = 3;
        this.frameCount = 8;


        image = new Bitmap[8][4];
        spritesheet = Bitmap.createScaledBitmap(res, 128, 64, false);
        // TODO: Use something other than magic numbers
        for(int i = 0; i<8; i++){
            for(int j = 0; j < 4; j++)
                image[i][j] = Bitmap.createBitmap(spritesheet, i*width, height * j, width, height);
        }

        startTime = System.nanoTime();
    }


    public void setHead(Bitmap res){
        this.head = true;
        spritesheet = Bitmap.createScaledBitmap(res, 128, 64, false);
        for(int i = 0; i<8; i++){
            for(int j = 0; j < 4; j++)
                image[i][j] = Bitmap.createBitmap(spritesheet, i*width, height * j, width, height);
        }
    }

    public void mushroomCollision(Mushroom mushroom, boolean isInfected){
        if(isInfected){

        }else{
            if(directionCode == 0){
                prevDirectionCode = 0;
                x = mushroom.getX() + mushroom.getWidth();
            }else if(directionCode == 1){
                prevDirectionCode = 1;
                x = mushroom.getX() - width;
            }else{
                return;
            }

            dx = 0;
            dy = 8;
            y+=8;
            directionCode = 2;
        }
    }

    public void draw(Canvas canvas){

        if(prevDirectionCode == 0 && directionCode == 2 || directionCode == 3)
            canvas.drawBitmap(image[currentFrame % 4][directionCode],x,y, null);
        else if(prevDirectionCode == 1 && directionCode == 2)
            canvas.drawBitmap(image[4+(currentFrame % 4)][directionCode],x,y, null);
        else
            canvas.drawBitmap(image[currentFrame][directionCode],x,y, null);

    }

    public void update(){
        if(y < 16)
            dy = 8;
        else if(y >= 512){
            dx = 0;
            dy =-8;
        }
        else if(x < 0){
            x = 0;
            dx = 0;
            dy = 8;
            prev2DirectionCode = prevDirectionCode;
            prevDirectionCode = directionCode;
            directionCode = 2;
        }
        else if(x + width > 480){
            x = 480 - width;
            dx = 0;
            dy = 8;
            prev2DirectionCode = prevDirectionCode;
            prevDirectionCode = directionCode;
            directionCode = 2;
        }
        else if(y == 16) {
            dy = 0;
            dx = 8;
            prev2DirectionCode = prevDirectionCode;
            prevDirectionCode = directionCode;
            directionCode = 1;
        }
        else if(directionCode == 2) {
            prev2DirectionCode = prevDirectionCode;
            prevDirectionCode = directionCode;
            directionCode = 3;
        }
        else if(directionCode == 3 ){
            if(prev2DirectionCode == 0) {
                prev2DirectionCode = prevDirectionCode;
                prevDirectionCode = directionCode;
                dy=0;
                dx = 8;
                directionCode =1;
            }else{
                prev2DirectionCode = prevDirectionCode;
                prevDirectionCode = directionCode;
                dy= 0;
                dx =-8;
                directionCode =0;
            }

        }else{
            prev2DirectionCode = prevDirectionCode;
            prevDirectionCode = directionCode;
        }

        long delay = 10;

        long elapsed = (System.nanoTime() - startTime)/1000000;


        if(elapsed > delay){
            currentFrame++;
            startTime = System.nanoTime();
        }
        if(currentFrame == frameCount){
            currentFrame = 0;
        }
        prev2X = prevX;
        prev2Y = prevY;
        prevX = x;
        prevY = y;
        x+= dx;
        y+= dy;
    }

    public int nextValidMushroomPositionX(){
        if(directionCode == 0){
                if (x < 15)
                    return -1;
                return((16* (x/16)) - 16);
        }else if(directionCode == 1){
            if(x + width > 479)
                return -1;
            return ((16* (x/16)) + 16);

        }else{
            return (16* (x/16));
        }
    }


    public boolean isHead(){
        return head;
    }

    public int getDirectionCode(){
        return directionCode;
    }

    public int getPrevX() {
        return prevX;
    }

    public void setPrevX(int prevX) {
        this.prevX = prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public void setPrevY(int prevY) {
        this.prevY = prevY;
    }
}
