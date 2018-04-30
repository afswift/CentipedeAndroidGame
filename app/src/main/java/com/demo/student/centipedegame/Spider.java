package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by butle on 4/16/2018.
 */

public class Spider extends GameObject {
    Bitmap[] image;
    private long startTime;
    private int frameCount;
    private int currentFrame;
    private long changeDirectionTime;
    long respawnTime;
    private boolean fromLeft;
    Random rand;


    Spider(Bitmap[] res, int x, int y, boolean fromLeft){
        this.width = 16;
        this.height = 8;
        this.x = x;
        this.y = y;
        image = res;
        this.frameCount = 8;
        startTime = System.nanoTime()/1000000;
        rand = new Random();
        respawnTime = startTime + 5000;
    }

    public void update(){
        int xDirection;
        if(fromLeft){
            xDirection = 4;
        }else{
            xDirection = -4;
        }
        if(changeDirectionTime < (System.nanoTime()/1000000)){
            changeDirectionTime =  (rand.nextInt(10) * 200) +  System.nanoTime()/1000000;
            if(rand.nextInt(2) == 0) {
                dy = 8 * -1;
            }else{
                dy = 8;
            }
            dx = xDirection * rand.nextInt(2);
        }else if( y > 508 - height){
            dy = -8;
            dx = xDirection * rand.nextInt(2);
            changeDirectionTime =  (rand.nextInt(5) * 400) +  System.nanoTime()/1000000;
        }else if( y < 288 ){
            changeDirectionTime =  (rand.nextInt(5) * 400) +  System.nanoTime()/1000000;
            dy = 8;
            dx = xDirection * rand.nextInt(2);;
        }
        y+=dy;
        x+=dx;
        updateAnimation();

    }

    public boolean isOffscreen(){
        if(fromLeft && x > 480){
            return true;
        }
        if(!fromLeft && x+width < 0)
            return true;

        return false;
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

    public void draw(Canvas canvas){
        canvas.drawBitmap(image[currentFrame],x,y, null);
    }

    public int onDeath(Player player){
        respawnTime = 1000* rand.nextInt(10);
        int xDist  =Math.abs(player.getxPrev() - this.x);
        int yDist  =Math.abs(player.getyPrev() - this.y);
        if(xDist + yDist > 30){
            return 900;
        }else if(xDist+yDist > 60){
            return 600;
        }else{
            return 300;
        }
    }

    public boolean isReadyToSpawn(){
        if(respawnTime < System.nanoTime()/1000000 && isOffscreen())
            return true;
        return false;
    }

    public void spawnNewSpider(){
        respawnTime = (2000 * rand.nextInt(5))+ System.nanoTime()/1000000;
        if(rand.nextInt(2) == 0){
            fromLeft = true;
            x = -16;
            y = 480;
        }else{
            fromLeft = false;
            x= 512;
            y=480;
        }
    }

    public void mushroomCollision(){

    }

}
