package com.demo.student.centipedegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by butle on 3/17/2018.
 */

class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 856;
    private GameThread thread;
    private Player player;
    private Random rand = new Random();
    private ArcadeButton rightButton;
    private Joystick leftJoystick;
    private ArrayList<Mushroom> mushroomArrayList;
    private ArrayList<Centipede> centipedeArrayList;
    private Laser laser;
    private Paint testPaint;
    private Paint test2Paint;
    private Paint scorePaint;
    private long newCentipedeTimer = 0;

    private boolean centipedeSpawnBottom = false;

    private boolean ignoreCollision;
    private int mushroomIndex;
    private int centipedeIndex;
    private int score = 0;
    private int numberOfLives = 3;
    private boolean gameOver;
    private int nextLifeScore = 10000;


    public GamePanel(Context context){
        super(context);

        // add the callback to the surfaceHolder to intercept events
        getHolder().addCallback(this);

        thread = new GameThread(getHolder(), this);
        //
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(thread==null) {
            thread = new GameThread(getHolder(), this);
            System.out.println("A New Thread Was Created");
            //
            setFocusable(true);
        }else {
            player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player));
            laser = new Laser();

            gameOver = false;

            centipedeArrayList = new ArrayList<Centipede>();
            centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head), true, 15 * 16, 0));
            for (int i = 1; i < 16; i++) {
                centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_body), false, (15) * 16, i * -16));

            }

            //for(int i = 0; i< centipedeArrayList.size();i++){
            //    centipedeArrayList.get(i).setHead(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head));
            //}

            mushroomArrayList = new ArrayList<Mushroom>();

            for (int i = 0; i < 23; i++) {
                rand.nextInt(30);
                mushroomArrayList.add(new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms), (16 * rand.nextInt(30)), (16 * (3 + rand.nextInt(27)))));
            }


        }

        mushroomIndex = -1;
        centipedeIndex = -1;
        ignoreCollision = false;

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(40);

        scorePaint = new Paint();
        scorePaint.setStyle(Paint.Style.FILL);
        scorePaint.setColor(Color.RED);
        scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


        Paint smallPaint = new Paint();
        smallPaint.setColor(Color.BLACK);
        smallPaint.setStyle(Paint.Style.STROKE);

        Paint buttonPressedPaint = new Paint();
        buttonPressedPaint.setColor(Color.BLUE);
        buttonPressedPaint.setDither(true);
        buttonPressedPaint.setStyle(Paint.Style.FILL);

        testPaint = new Paint();
        testPaint.setColor(Color.BLACK);
        testPaint.setStyle(Paint.Style.FILL);

        test2Paint = new Paint();
        test2Paint.setColor(Color.BLUE);
        test2Paint.setStyle(Paint.Style.FILL);

        leftJoystick = new Joystick(WIDTH - 380, HEIGHT - 175, 75, paint, smallPaint);
        rightButton = new ArcadeButton(WIDTH - 100, HEIGHT - 175, 75, paint, buttonPressedPaint);

        thread.setRunning(true);
        if(!thread.isAlive())
            thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        int counter = 0;
        while(retry && counter < 1000){
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            }catch(InterruptedException e){e.printStackTrace();}
        }
        thread = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float widthScale = getWidth()/(WIDTH*1.f);
        float heightScale = getHeight()/(HEIGHT*1.f);

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying()){
                player.setPlaying(true);
            }
            //return true;
        }
        rightButton.onMotionEvent(event, widthScale, heightScale);
        leftJoystick.onMotionEvent(event, widthScale, heightScale);
        if(!leftJoystick.isIdle())
            player.setDirection(leftJoystick.getJoystickAngle(), leftJoystick.getJoystickeMagnitude());
        else
            player.setNeutral();

        return true;
    }

    // **********************
    // **** UPDATE EVENT ****
    // **********************

    public void update(){
        if(player.getPlaying()) {
            laser.update();
            player.update();

            ArrayList<Integer> centipedeHeadReference = new ArrayList<Integer>();

            if((System.nanoTime()/1000000) - newCentipedeTimer   > 4000 && centipedeSpawnBottom){
                centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head),true, 488* rand.nextInt(2), 16*25));
                newCentipedeTimer =  (System.nanoTime()/1000000);
            }
            //System.out.println("Centipede Y: " + centipedeArrayList.get(0).getY() % 16);
            for(int i = 0; i < centipedeArrayList.size();i++ ) {
                if(centipedeArrayList.get(i).isHead()) {
                    centipedeArrayList.get(i).update();

                }else {
                    if( i > 0 )
                    centipedeArrayList.get(i).updateBody(centipedeArrayList.get(i-1));
                }
                if(centipedeArrayList.get(i).getY() >= 512-16 && !centipedeSpawnBottom){
                    centipedeSpawnBottom = true;
                    newCentipedeTimer = System.nanoTime()/1000000;
                }
            }

            if(rightButton.isButtonPressed() && !laser.isLaserOnscreen())
                laser.resetLaser(player.getX() + player.getWidth()/2, player.getY());

            int tempMushroomIndex = -1;
            Mushroom tempMushroomReference = null;
            for(int i = 0; i < mushroomArrayList.size(); i++){

                // checking for laser and mushroom collision
                if(collision(mushroomArrayList.get(i), laser)){
                    mushroomArrayList.get(i).decrementHitpoints();
                    if(mushroomArrayList.get(i).getHitpoints() < 1) {
                        mushroomArrayList.remove(i);
                        score +=1;
                    }
                    laser.stopFiring();
                }

                // checking mushroom and player collision
                if(collision(mushroomArrayList.get(i), player)){
                    tempMushroomReference = mushroomArrayList.get(i);

                    // from right
                    if(player.getxPrev() >= (tempMushroomReference.getX()+ tempMushroomReference.getWidth())){
                        player.setX(tempMushroomReference.getX() + tempMushroomReference.getWidth());//+1);
                        player.setDx(0);
                    }
                    // from left
                    else if(player.getxPrev() + player.getWidth() <= tempMushroomReference.getX()){
                        player.setX(tempMushroomReference.getX() - player.getWidth()); // - 1);
                        player.setDx(0);
                    }
                    else if((player.getyPrev() + player.getHeight()) <= tempMushroomReference.getY()){
                        player.setY(tempMushroomReference.getY()- player.getHeight()); //-1);
                        player.setDy(0);
                    }
                    // from right
                    else if(player.getyPrev() >= (tempMushroomReference.getY()+ tempMushroomReference.getHeight())){
                        player.setY(tempMushroomReference.getY() + tempMushroomReference.getHeight());//+1);
                        player.setDy(0);
                    }
                }

                // checking for centipede and mushroom collision
                for(int j = 0; j < centipedeArrayList.size(); j++){
                    if (centipedeArrayList.get(j).isHead() && collision(mushroomArrayList.get(i), centipedeArrayList.get(j))) {
                        centipedeArrayList.get(j).mushroomCollision(mushroomArrayList.get(i));
                    }

                    if(collision(centipedeArrayList.get(j), player)){

                        if(numberOfLives > 0) {
                            resetScene();
                            numberOfLives--;
                        }
                        else{
                            gameOver = true;
                            player.setPlaying(false);
                        }

                    }

                    if(collision(centipedeArrayList.get(j), laser)) {
                        if( j + 1 < centipedeArrayList.size()){
                            centipedeArrayList.get(j+1).setHead(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head));

                        }
                        if(centipedeArrayList.get(j).isHead()){
                            score += 100;
                        }else{
                            score += 10;
                        }

                        if(centipedeArrayList.get(j).nextValidMushroomPositionX() != -1)
                            mushroomArrayList.add(new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms),
                                    centipedeArrayList.get(j).nextValidMushroomPositionX(),centipedeArrayList.get(j).getY(), 4 ));
                        laser.stopFiring();

                        if(j - 1 > -1)
                            centipedeIndex = j-1;

                        centipedeArrayList.remove(j);

                        if(centipedeArrayList.size() == 0)
                            spawnNewCentipede();
                        mushroomIndex = mushroomArrayList.size() -1;
                        ignoreCollision = true;
                    }
                }
            }

            boolean newSegment = false;
            for(int i = 0 ; i < centipedeArrayList.size(); i++){
                for(int j = 1; j < centipedeArrayList.size(); j++){
                    if(i!=j && centipedeArrayList.get(i).isHead()){
                        if(collision(centipedeArrayList.get(i), centipedeArrayList.get(j))){
                            if(centipedeArrayList.get(j).isHead()){
                                newSegment = true;
                            }
                            if(newSegment)
                                centipedeArrayList.get(i).centipedeCollision(centipedeArrayList.get(j));
                        }

                    }
                }
                newSegment = false;
            }

            if(tempMushroomIndex!= -1)
                mushroomArrayList.remove(tempMushroomIndex);


        }
        if(score >= nextLifeScore){
            numberOfLives++;
            nextLifeScore+=nextLifeScore;
        }
    }

    public boolean collision(GameObject a, GameObject b){
        if(Rect.intersects(a.getRectangle(),b.getRectangle())){
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleFactorX = getWidth() / (WIDTH*1.f);
        final float scaleFactorY = getHeight() / (HEIGHT*1.f);
        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            canvas.drawRect(0, 0, 480, 520, test2Paint);
            canvas.drawRect(0, 0, 480, 514, testPaint);
            //background.draw(canvas);
            player.draw(canvas);
            leftJoystick.draw(canvas);
            rightButton.draw(canvas);

            // mushroomTest.draw(canvas)
            for(int i = 0; i < mushroomArrayList.size(); i++){
                mushroomArrayList.get(i).draw(canvas);
            }
            laser.draw(canvas);
            for(int i = 0; i < centipedeArrayList.size(); i++){
                centipedeArrayList.get(i).draw(canvas);
            }


            if(gameOver){
                canvas.drawText("GAME OVER", WIDTH/2 , HEIGHT, scorePaint);
            }else{
                canvas.drawText(Integer.toString(score), WIDTH/2 , 20, scorePaint);
            }
            // restore so image doesn't get larger and larger
            canvas.restoreToCount(savedState);

        }
    }

    public void resetScene(){
        int tempMushroomCount =  mushroomArrayList.size();
        for(int i = 0; i<tempMushroomCount; i++){
            mushroomArrayList.get(i).setHitpoints(4);
        }
        for(int i = tempMushroomCount -1; i< 30; i++){
            score+=5; // upon regenerating a mushroom, player gets 5 points for each mushroom destroyed
            mushroomArrayList.add(new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms),(16 * rand.nextInt(30)), (16 * (3 + rand.nextInt(27))) ));
        }
        spawnNewCentipede();
    }

    public void spawnNewCentipede(){
        centipedeArrayList = null;
        centipedeArrayList = new ArrayList<Centipede>();
        centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head),true, 15*16, 0));
        for(int i = 1; i< 16; i++){
            centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_body),false, (15)*16, i*-16));

        }
        centipedeSpawnBottom = false;
    }

    public int[] savePlayerPosition(){
        int [] playerPosition = new int[2];
        playerPosition[0] = player.getX();
        playerPosition[1]= player.getY();
        return playerPosition;
    }

    public int[] saveCentipedePosition(){
        int []centipedePosition = new int[5*centipedeArrayList.size()];
        for(int i = 0; i<centipedeArrayList.size(); i++){
            centipedePosition[(5*i)] = centipedeArrayList.get(i).getX();
            centipedePosition[(5*i)+1] = centipedeArrayList.get(i).getY();
            centipedePosition[(5*i)+2] = centipedeArrayList.get(i).getDx();
            centipedePosition[(5*i)+3] = centipedeArrayList.get(i).getDy();
            centipedePosition[(5*i)+4] = centipedeArrayList.get(i).getDirectionCode();
        }
        return centipedePosition;
    }

    public boolean[] saveCentipedeBoolean(){
        boolean []centipedeBooleans = new boolean[2*centipedeArrayList.size()];
        for(int i = 0; i<centipedeArrayList.size(); i++){
            centipedeBooleans[(2*i)] = centipedeArrayList.get(i).isHead();
            centipedeBooleans[(2*i)+1] = centipedeArrayList.get(i).hasReachedBottom();
        }

        return centipedeBooleans;
    }

    public int[] saveMushroomsPosition(){
        int [] mushroomPosition = new int[3*mushroomArrayList.size()];
        for(int i = 0; i<mushroomArrayList.size(); i++){
            mushroomPosition[(3*i)] = mushroomArrayList.get(i).getX();
            mushroomPosition[(3*i)+1] = mushroomArrayList.get(i).getY();
            mushroomPosition[(3*i)+2] = mushroomArrayList.get(i).getHitpoints();
        }
        return mushroomPosition;
    }

    public void reloadPlayerPosition(int[] playerPosition){
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player),playerPosition[0], playerPosition[1]);
    }

    public void reloadCentipede(Bundle savedBundle){
        centipedeArrayList = null;
        centipedeArrayList = new ArrayList<Centipede>();

        int[] tempIntArray = savedBundle.getIntArray("centipedeMovement");
        int adjustedArraySize = tempIntArray.length/5;
        boolean[] tempBooleanArray = savedBundle.getBooleanArray("centpedeBooleans");
        for(int i = 0; i< adjustedArraySize; i++){
            centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head),
                    tempBooleanArray[i*2], tempBooleanArray[(i*2)+1], tempIntArray[i*5],tempIntArray[(i*5)+1],tempIntArray[(i*5)+2],tempIntArray[(i*5)+3],
                    tempIntArray[(i*5)+4]));
        }
    }

    public void reloadMushrooms(int[] mushroomPosition){
        mushroomArrayList = null;
        mushroomArrayList = new ArrayList<Mushroom>();
        int adjustedArraySize = mushroomPosition.length/3;
        for(int i = 0; i< adjustedArraySize; i++){
            mushroomArrayList.add( new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms),
                    mushroomPosition[i*3],mushroomPosition[(i*3)+1], mushroomPosition[(i*3)+2]));
        }
    }

}
