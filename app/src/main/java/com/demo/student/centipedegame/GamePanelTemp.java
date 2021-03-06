package com.demo.student.centipedegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by butle on 3/17/2018.
 */

class GamePanelTemp extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 480;
    public static final int HEIGHT = 856;
    private GameThread thread;
    private Background background;
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
    private Centipede centipedeRef;
    private boolean ignoreCollision;
    private int mushroomIndex;
    private int centipedeIndex;
    private int score = 0;


    public GamePanelTemp(Context context){
        super(context);

        // add the callback to the surfaceHolder to intercept events
        getHolder().addCallback(this);

        //thread = new GameThread(getHolder(), this);
        //
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player),14,16);
        laser = new Laser();

        centipedeArrayList = new ArrayList<Centipede>();
        centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head),true, 15*16, 0));
        for(int i = 1; i< 16; i++){
            centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_body),false, (15)*16, i*-16));

        }

        //for(int i = 0; i< centipedeArrayList.size();i++){
        //    centipedeArrayList.get(i).setHead(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head));
        //}

        mushroomArrayList = new ArrayList<Mushroom>();

        for(int i = 0; i <  23; i++){
            rand.nextInt(30);
            mushroomArrayList.add(new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms),(16 * rand.nextInt(30)), (16 * (3 + rand.nextInt(27))), 4 ));
        }

        mushroomIndex = -1;
        centipedeIndex = -1;
        ignoreCollision = false;
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(20);

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

    public void update(){
        if(player.getPlaying()) {
            background.update();
            laser.update();
            player.update();

            //System.out.println("Centipede Y: " + centipedeArrayList.get(0).getY() % 16);
            for(int i = 0; i < centipedeArrayList.size();i++ ) {
                    centipedeArrayList.get(i).update();
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
                    if(j == centipedeIndex && i == mushroomIndex && ignoreCollision) {
                        if (collision(mushroomArrayList.get(mushroomIndex), centipedeArrayList.get(centipedeIndex))) {
                            ignoreCollision = true;
                        }else{
                            ignoreCollision = false;
                        }
                    }else{
                        if (collision(mushroomArrayList.get(i), centipedeArrayList.get(j))) {
                            centipedeArrayList.get(j).mushroomCollision(mushroomArrayList.get(i), false);
                        }
                    }

                    if(collision(centipedeArrayList.get(j), player)){
                        resetScene();
                    }

                    if(collision(centipedeArrayList.get(j), laser)) {
                        if( j + 1 < centipedeArrayList.size()){
                            centipedeArrayList.get(j+1).setHead(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head));
                        }
                        if(centipedeArrayList.get(j).isHead()){
                            score += 100;
                        }

                        if(centipedeArrayList.get(j).nextValidMushroomPositionX() != -1)
                        mushroomArrayList.add(new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms),
                                centipedeArrayList.get(j).nextValidMushroomPositionX(),centipedeArrayList.get(j).getY(), 4 ));
                        laser.stopFiring();
                        centipedeArrayList.remove(j);
                        if(j - 1 > -1)
                            centipedeIndex = j-1;
                        //j--;
                        mushroomIndex = mushroomArrayList.size() -1;
                        ignoreCollision = true;
                    }
                }
            }
            if(tempMushroomIndex!= -1)
                mushroomArrayList.remove(tempMushroomIndex);


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
        for(int i = tempMushroomCount -1; i< 43; i++){
            score+=5; // upon regenerating a mushroom, player gets 5 points for each mushroom destroyed
            mushroomArrayList.add(new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms),(16 * rand.nextInt(30)), (16 * (3 + rand.nextInt(27))), 4 ));
        }
        centipedeArrayList = null;
        centipedeArrayList = new ArrayList<Centipede>();
        centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_head),true, 15*16, 0));
        for(int i = 1; i< 16; i++){
            centipedeArrayList.add(new Centipede(BitmapFactory.decodeResource(getResources(), R.drawable.centipede_body),false, (15)*16, i*-16));

        }
    }
}
