package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by butle on 3/18/2018.
 */

public class Background {

    private Bitmap image;
    private int x;
    private int y;
    private int dx;

    public Background(Bitmap res){
        image = res;
    }

    public void update(){
        //x+=dx;
        if(x<-GamePanel.WIDTH){
            x=0;
        }
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image,x, y, null);
        if(x<0){
            canvas.drawBitmap(image, x+GamePanel.WIDTH, y, null);
        }
    }

    public void setVector(int dx){
        this.dx = dx;
    }
}
