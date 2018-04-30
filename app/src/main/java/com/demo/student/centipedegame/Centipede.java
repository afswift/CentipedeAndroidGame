package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by butle on 4/1/2018.
 */

public class Centipede extends GameObject {
    private long startTime;
    private boolean head;
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
    private boolean centipedeReachedBottom;

    // directionCode 0 is left, 1 is right, 2 is diag, 3 is down

    public Centipede(Bitmap[][] splitSpritesheet, boolean isHead, int x, int y){
        this.head = isHead;
        this.width = 16;
        this.height = 16;
        this.x = x;
        this.y = y;
        this.directionCode = 3;
        this.frameCount = 8;
        centipedeReachedBottom = false;
        this.image =  splitSpritesheet;
        startTime = System.nanoTime();
    }

    public Centipede(Bitmap[][] res, boolean isHead,boolean reachedBottom, int x, int y, int dx, int dy, int directionCode){
        this.head = isHead;
        this.width = 16;
        this.height = 16;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.directionCode = directionCode;
        this.frameCount = 8;
        centipedeReachedBottom = reachedBottom;

        image = res;
        startTime = System.nanoTime();
    }


    public void setHead(Bitmap[][] res){
        this.head = true;
        if(directionCode== 0){
            dx = -8;
        }else if(directionCode == 1){
            dx = 8;
        }else if(directionCode == 2){
            if(centipedeReachedBottom)
                dy = -8;
            else
                dy = 8;


        }else{
            if(prev2DirectionCode == 0)
                dx = 8;
            else if(prev2DirectionCode == 1)
                dx = -8;
        }
        image = res;

    }

    public void mushroomCollision(Mushroom mushroom){

        if(directionCode == 0){
            prev2DirectionCode=prevDirectionCode;
            prevDirectionCode = 0;
            x = mushroom.getX() + mushroom.getWidth();
            y+=8;
        }else if(directionCode == 1){
            prev2DirectionCode=prevDirectionCode;
            prevDirectionCode = 1;
            x = mushroom.getX() - width;
            y+=8;
        }else{
            return;
        }
        directionCode=2;
        dx = 0;

        if(!centipedeReachedBottom) {
            y += 8;
            //dy = 8;
        }
        else {
            y -= 8;
            //dy = -8;
        }
        directionCode = 2;
    }


    public void draw(Canvas canvas){
        if(prevDirectionCode == 0 && directionCode == 2 || directionCode == 3)
            canvas.drawBitmap(image[currentFrame % 4][directionCode],x,y, null);
        else if(prevDirectionCode == 1 && directionCode == 2)
            canvas.drawBitmap(image[4+(currentFrame % 4)][directionCode],x,y, null);
        else
            canvas.drawBitmap(image[currentFrame][directionCode],x,y, null);
    }
/*
    public void update(){

    }
*/
    public void update(){
        if(y < 16)
            dy = 8;
        else if(y >= 512-16){
            y = 512-16;
            dx = 0;
            dy =-8;
            centipedeReachedBottom = true;
        }
        else if(x < 0){
            x = 0;
            dx = 0;
            if(!centipedeReachedBottom)
                dy = 8;
            else{
                dy = -8;
            }
            prev2DirectionCode = prevDirectionCode;
            prevDirectionCode = directionCode;
            directionCode = 2;
        }
        else if(x + width > 480){
            x = 480 - width;
            dx = 0;
            if(!centipedeReachedBottom)
                dy = 8;
            else
                dy = -8;
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
            dy = 0;
            dx = 0;
        }
        else if(directionCode == 3 ){
            dy=0;
            if(centipedeReachedBottom){
                y-=8;
            }else{
                y+=8;
            }
            if(prev2DirectionCode == 0) {
                prev2DirectionCode = prevDirectionCode;
                prevDirectionCode = directionCode;
                dx = 8;
                directionCode =1;
            }else{
                prev2DirectionCode = prevDirectionCode;
                prevDirectionCode = directionCode;

                dx =-8;
                directionCode =0;
            }

        }else{
            prev2DirectionCode = prevDirectionCode;
            prevDirectionCode = directionCode;
        }

       updateAnimation();;

        prev2X = prevX;
        prev2Y = prevY;
        prevX = x;
        prevY = y;
        x+= dx;
        y+= dy;

        if(y < 16 * 26)
            centipedeReachedBottom = false;
    }



    public void updateAnimation(){
        long delay = 10;
        long elapsed = (System.nanoTime() - startTime)/1000000;
        if(elapsed > delay){
            currentFrame++;
            startTime = System.nanoTime();
        }
        if(currentFrame == frameCount){
            currentFrame = 0;
        }
    }

    public void updateBody(Centipede newHead){

        updateAnimation();

        this.prev2X = this.prevX;
        this.prev2Y = this.prevY;
        this.prevX = this.x;
        this.prevY = this.y;
        this.x = newHead.prev2X;
        this.y = newHead.prev2Y;

        this.prev2DirectionCode = this.prevDirectionCode;
        this.prevDirectionCode = this.directionCode;
        this.directionCode = newHead.prev2DirectionCode;

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

    public boolean hasReachedBottom(){
        return centipedeReachedBottom;
    }

    public Rect getNextCollisionRect(){
        return new Rect(x+(dx),y, (dx) + x+width, y+height);
    }
}
