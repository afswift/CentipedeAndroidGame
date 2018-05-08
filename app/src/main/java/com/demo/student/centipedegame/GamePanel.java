package com.demo.student.centipedegame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by butle on 3/17/2018.
 */

interface GamePanelBooleanChangedListener {
    public void OnMyBooleanChanged();
}



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
    private Spider spider;
    private Paint testPaint;
    private Paint test2Paint;
    private Paint scorePaint;
    private long newCentipedeTimer = 0;
    private Bitmap[][] centipedeHeadSprites;
    private Bitmap[][] centipedeBodySprites;
    private Bitmap[] spiderSprites;
    private boolean isPlaying = false;
    private boolean centipedeSpawnBottom = false;
    private int score = 0;
    private int numberOfLives = 0;
    private boolean gameOver;
    private int nextLifeScore = 10000;
    private static List<GamePanelBooleanChangedListener> listeners = new ArrayList<GamePanelBooleanChangedListener>();
    private Mushroom[][] mushroomField;


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
        centipedeHeadSprites = new Bitmap[8][4];
        centipedeBodySprites = new Bitmap[8][4];
        spiderSprites = new Bitmap[8];

        Bitmap centipedeHeadSpritesheet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head), 128, 64, false);
        Bitmap centipedeBodySpritesheet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_body), 128, 64, false);
        Bitmap spiderSpritesheet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.spider), 256, 16, false);
        // TODO: Use something other than magic numbers
        for(int i = 0; i<8; i++){
                spiderSprites[i] =  Bitmap.createBitmap(spiderSpritesheet, i*32, 0, 32, 16);
            for(int j = 0; j < 4; j++){
                centipedeHeadSprites[i][j] = Bitmap.createBitmap(centipedeHeadSpritesheet, i*16, 16 * j, 16, 16);
                centipedeBodySprites[i][j] = Bitmap.createBitmap(centipedeBodySpritesheet, i*16, 16 * j, 16, 16);
            }
        }

        spider = new Spider(spiderSprites, -16, 480, true);


        if(thread==null) {
            thread = new GameThread(getHolder(), this);
            isPlaying = false;
            setFocusable(true);
        }else {
            player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player));
            laser = new Laser();
            gameOver = false;
            centipedeArrayList = new ArrayList<Centipede>();
            mushroomArrayList = new ArrayList<Mushroom>();

            mushroomField = new Mushroom[30][30];

            centipedeArrayList.add(new Centipede(centipedeHeadSprites, true, 15 * 16, 0));
            for (int i = 1; i < 16; i++)
                centipedeArrayList.add(new Centipede(centipedeBodySprites, false, (15) * 16, i * -16));

            for (int i = 0; i < 23; i++) {
                //rand.nextInt(30);
                mushroomArrayList.add(new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms), (16 * rand.nextInt(30)), (16 * (3 + rand.nextInt(27)))));
            }

            /*
            for(int i = 0; i < 30; i++){
                for(int j = 0; j< 30; j++){
                    mushroomField[i][j]=null;
                }
            }
            for (int i = 0; i < 23; i++) {
                mushroomXPos = rand.nextInt(30);
                mushroomYPos = 3 + rand.nextInt(27);
                mushroomField[mushroomXPos][mushroomYPos] = new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms), (16 * mushroomXPos), (16 * (3 + mushroomYPos)));
            }
            */
            isPlaying = true;
        }

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

        leftJoystick = new Joystick(WIDTH - 340, HEIGHT - 175, 75, paint, smallPaint);
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

        if(event.getAction()==MotionEvent.ACTION_DOWN && event.getY()/heightScale > 512){
            if(!isPlaying){
                isPlaying=true;
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
        if(isPlaying) {
            laser.update();
            player.update();

            if(spider.isReadyToSpawn()){
                spider.spawnNewSpider();
            }else
                spider.update();

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

            if((System.nanoTime()/1000000) - newCentipedeTimer   > 4000 && centipedeSpawnBottom){
                centipedeArrayList.add(new Centipede( centipedeHeadSprites,true, 488* rand.nextInt(2), 16*25));
                newCentipedeTimer =  (System.nanoTime()/1000000);
            }



            if(rightButton.isButtonPressed() && !laser.isLaserOnscreen())
                laser.resetLaser(player.getX() + player.getWidth()/2, player.getY());


            /*
            Mushroom mushroomReference = null;
            if(laser.getX() > 0 && laser.getX() < 480 ) {
                if (laser.getY() > 0 && laser.getY() < 512) {
                    if (mushroomField[laser.getX() / 16][laser.getY() / 16] != null) {
                        mushroomReference = mushroomField[laser.getX() / 16][laser.getY() / 16];
                        if (collision(mushroomReference, laser)) {
                            mushroomReference.decrementHitpoints();
                            if (mushroomReference.getHitpoints() < 1) {
                                mushroomField[laser.getX() / 16][laser.getY() / 16] = null;
                                score += 1;
                            }
                        }
                    }
                }
            }
            */


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
                    player.solidObjectCollision(mushroomArrayList.get(i));
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
                            setGameOver(true);
                            isPlaying = false;
                        }

                    }

                    if(collision(centipedeArrayList.get(j), laser)) {
                        if( j + 1 < centipedeArrayList.size()){
                            centipedeArrayList.get(j+1).setHead(centipedeHeadSprites);
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



                        centipedeArrayList.remove(j);

                        if(centipedeArrayList.size() == 0)
                            spawnNewCentipede();
                    }
                }
            }

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

    public boolean collisionPredicted(GameObject a, Centipede b){
        if(Rect.intersects(a.getRectangle(),b.getNextCollisionRect())){
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
            //background = Bitmap.createScaledBitmap(background,480,(856-512), false);
            //canvas.drawBitmap(background,0, 515, null  );

            leftJoystick.draw(canvas);
            rightButton.draw(canvas);
            if(gameOver){
                canvas.drawText("GAME OVER", WIDTH/2 , HEIGHT/2, scorePaint);
            }else if(isPlaying) {
                player.draw(canvas);
                // mushroomTest.draw(canvas)
                for (int i = 0; i < mushroomArrayList.size(); i++) {
                    mushroomArrayList.get(i).draw(canvas);
                }
                laser.draw(canvas);
                for (int i = 0; i < centipedeArrayList.size(); i++) {
                    centipedeArrayList.get(i).draw(canvas);
                }
                if(spider!=null)
                    spider.draw(canvas);

            }else{
                test2Paint.setTextSize(WIDTH/10);
                canvas.drawText("PAUSED", WIDTH/3, HEIGHT/3, test2Paint);
            }

            canvas.drawText(Integer.toString(score), WIDTH/2 , 20, scorePaint);
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
        centipedeArrayList.add(new Centipede(centipedeHeadSprites,true, 15*16, 0));
        for(int i = 1; i< 16; i++){
            centipedeArrayList.add(new Centipede(centipedeBodySprites,false, (15)*16, i*-16));

        }
        centipedeSpawnBottom = false;
    }

    public void spawnNewSpider(){

    }


    /******************************************
       SAVING STATE AND RELOADING STATE VALUES*

     */
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
            centipedeArrayList.add(new Centipede(centipedeHeadSprites,
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


    public int getScore(){
        return score;
    }

    public void setIsPlaying(boolean b){
        isPlaying =b;
    }

    public boolean getIsPlaying(){
        return isPlaying;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        for(GamePanelBooleanChangedListener l: listeners){
            l.OnMyBooleanChanged();
        }
    }

    public static void addGamePanelBooleanListener(GamePanelBooleanChangedListener l){
        listeners.add(l);
    }


}
