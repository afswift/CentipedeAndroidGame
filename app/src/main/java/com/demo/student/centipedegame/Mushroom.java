package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by butle on 3/30/2018.
 */

public class Mushroom extends GameObject {

    private int hitpoints;
    private boolean infected;
    private Bitmap spritesheet;
    private Bitmap[] mushroomSprites;

    public Mushroom(Bitmap res, int x, int y, int numFrames){
        this.x=x;
        this.y = y;
        spritesheet = Bitmap.createScaledBitmap(res, 64, 16, false);
        dy = 0;
        dx = 0;
        hitpoints = 4;
        width = 16;
        height = 16;
        mushroomSprites = new Bitmap[numFrames];
        infected = false;
        System.out.println(spritesheet.getHeight());
        System.out.println(spritesheet.getHeight());
        System.out.println(spritesheet.getHeight());
        for(int i = 0; i<mushroomSprites.length; i++){
            mushroomSprites[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public void decrementHitpoints(){
        hitpoints--;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public void draw(Canvas canvas){
        try {
            if(hitpoints > 0)
                canvas.drawBitmap(mushroomSprites[4-hitpoints], x, y, null);
        }catch(Exception e){}
    }

}
