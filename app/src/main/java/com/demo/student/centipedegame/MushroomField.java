package com.demo.student.centipedegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by butle on 3/31/2018.
 */

public class MushroomField {
    ArrayList<Mushroom> mushroomArrayList;
    private Random rand = new Random();

    MushroomField(Bitmap res){
        // initial mushroom count is approximately 41-43 and it may be random?
        mushroomArrayList = new ArrayList<Mushroom>();

        for(int i = 0; i < 43; i++){

            rand.nextInt(30);
            mushroomArrayList.add(new Mushroom(res,(16 * rand.nextInt(30)), (16 * (4 + rand.nextInt(23))), 4 ));
        }
    }

    public void draw(Canvas canvas){
        for(int i = 0; i < mushroomArrayList.size(); i++){
            mushroomArrayList.get(i).draw(canvas);
        }
    }

    public void removeMushroom(int index){
        mushroomArrayList.remove(index);
    }


}
