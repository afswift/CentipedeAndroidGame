package com.demo.student.centipedegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.test.suitebuilder.annotation.Smoke;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by butle on 3/17/2018.
 */

class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    private long missleStartTime;
    private GameThread thread;
    private Background background;
    private Player player;
    private ArrayList<Missle> missles;
    private Random rand = new Random();
    private ArcadeButton rightButton;
    private Joystick leftJoystick;
    private MushroomField mushroomField;

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
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter),65,24, 3);
        missles = new ArrayList<Missle>();


        mushroomField = new MushroomField(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms));
        //mushroomTest = new Mushroom(BitmapFactory.decodeResource(getResources(), R.drawable.mushrooms),65,24, 4);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        Paint smallPaint = new Paint();
        smallPaint.setColor(Color.BLACK);
        smallPaint.setStyle(Paint.Style.STROKE);

        Paint buttonPressedPaint = new Paint();
        buttonPressedPaint.setColor(Color.BLUE);
        buttonPressedPaint.setDither(true);
        buttonPressedPaint.setStyle(Paint.Style.FILL);

        leftJoystick = new Joystick(WIDTH - 100, HEIGHT - 100, 75, paint, smallPaint);
        rightButton = new ArcadeButton(WIDTH - 100, HEIGHT - 400, 75, paint, buttonPressedPaint);

        missleStartTime = System.nanoTime();
        background.setVector(-5);

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
            player.setDirection(leftJoystick.getJoystickAngle());
        else
            player.setNeutral();

        return true;
    }

    public void update(){
        if(player.getPlaying()) {
            background.update();
            player.update();
            long misslesElapsed = (System.nanoTime()-missleStartTime)/1000000;
            if(misslesElapsed > (2000- player.getScore()/4)){

                if(missles.size()==0){
                    missles.add(new Missle(BitmapFactory.decodeResource(getResources(),
                            R.drawable.missile),(int)WIDTH + 10, (int)HEIGHT/2, 45, 15,
                            player.getScore(), 13));
                }else{
                    missles.add(new Missle(BitmapFactory.decodeResource(getResources(),
                            R.drawable.missile),(int)WIDTH + 10, (int)(rand.nextDouble()*HEIGHT), 45, 15,
                            player.getScore(), 13));

                }
                // reset timer
                missleStartTime = System.nanoTime();
            }

            for(int i=0; i<missles.size();i++){
                missles.get(i).update();
                if(collision(missles.get(i),player)){
                    missles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                // remove missle if it is off screen;
                if(missles.get(i).getX()<-100){
                    missles.remove(i);
                    break;
                }
            }
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
            background.draw(canvas);
            player.draw(canvas);

            for(Missle m: missles )
                m.draw(canvas);

            leftJoystick.draw(canvas);
            rightButton.draw(canvas);

            // mushroomTest.draw(canvas);
            mushroomField.draw(canvas);



            // restore so image doesn't get larger and larger
            canvas.restoreToCount(savedState);

        }
    }
}
