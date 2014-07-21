package com.example.textcircle2;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnTouchListener {

    private ImageView mDestinationText;
    private TextView mSourceText;
    private Paint mTextPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int mBackColor;
    private int mBackColorEvil;
    private int mTextColor;
    private int mTextColorEvil;
    private boolean mEvil = false;
    private int mWidth;
    private int mHeight;

    private ValueAnimator mWipeAnim;
    private SeekBar mSeekbar;
    private Bitmap mBitmapBackup;
    private Animator.AnimatorListener mAnimlistener;
    private float mcircleCenterY;
    private float mcircleCenterX;

    private long mTimetoCrossWidth = 900;
    private Bitmap mBitmapCover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSourceText = (TextView) findViewById(R.id.text1);
        mDestinationText = (ImageView) findViewById(R.id.text2);

        mSeekbar = (SeekBar) findViewById(R.id.myProgress);



        mBackColor = Color.RED;
        mBackColorEvil = Color.GREEN;
        mTextColor = Color.BLACK;
        mTextColorEvil = Color.BLUE;

        mAnimlistener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.button).setEnabled(false);
                findViewById(R.id.myProgress).setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.button).setEnabled(true);
                findViewById(R.id.myProgress).setEnabled(true);

            }

        };

        mSeekbar.setEnabled(false);
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){

                    Canvas revealCanvas = new Canvas(mBitmap);

                    revealCanvas.drawBitmap(mBitmapBackup,0,0,new Paint());

                    revealCanvas.clipRect(getNewRect(progress));
                    revealCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    mDestinationText.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        mSourceText.post(new Runnable() {
            @Override
            public void run() {


                mWidth = mSourceText.getWidth();
                mHeight = mSourceText.getHeight();


                mSeekbar.setMax(mWidth);

                mWipeAnim = ValueAnimator.ofInt(0,mWidth);
                mWipeAnim.setDuration(1200);
                mWipeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        updateWipe((Integer)animation.getAnimatedValue());
                        mSeekbar.setProgress((Integer) animation.getAnimatedValue());
                    }
                });
                mWipeAnim.addListener(mAnimlistener);

            }
        });


        mSourceText.setOnTouchListener(this);
        mSourceText.setClickable(true);

    }

    private void updateWipe(Integer animatedValue) {
        Canvas revealCanvas = new Canvas(mBitmap);
        Rect revealRect = getNewRect(animatedValue);
        revealCanvas.clipRect(revealRect);
        revealCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mDestinationText.invalidate(revealRect);


    }

    private Rect getNewRect (int right){
        return new Rect(0,0,right,mHeight);
    }

    public void Button (View view){

        prepareWipe();

        mWipeAnim.start();



    }

    private void prepareWipe() {
        mSourceText.setDrawingCacheEnabled(true);
        // mSourceText.buildDrawingCache();
        mBitmap = Bitmap.createBitmap(mSourceText.getDrawingCache());
        mBitmapBackup = Bitmap.createBitmap(mSourceText.getDrawingCache());


        ImageView cover = new ImageView(this);

        cover.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((FrameLayout)findViewById(R.id.container)).addView(cover,0);
        cover.setX(mSourceText.getX());
        cover.setY(mSourceText.getY());

        mBitmap.setHasAlpha(true);
        mBitmapBackup.setHasAlpha(true);
        //mBitmapCover.setHasAlpha(true);
        mDestinationText.setX(mSourceText.getX());
        mDestinationText.setY(mSourceText.getY());
        mDestinationText.setImageBitmap(mBitmap);

        if(mEvil){
            mSourceText.setTextColor(mTextColor);
            mSourceText.setBackgroundColor(mBackColor);

            mBitmapCover = Bitmap.createBitmap(mSourceText.getDrawingCache());
            cover.setImageBitmap(mBitmapCover);

            mEvil=false;

            mSourceText.setTextColor(mTextColorEvil);
            mSourceText.setBackgroundColor(mBackColorEvil);
        }else{
            mSourceText.setTextColor(mTextColorEvil);
            mSourceText.setBackgroundColor(mBackColorEvil);

            mBitmapCover = Bitmap.createBitmap(mSourceText.getDrawingCache());
            cover.setImageBitmap(mBitmapCover);

            mEvil=true;

            mSourceText.setTextColor(mTextColor);
            mSourceText.setBackgroundColor(mBackColor);

        }
        mSourceText.setVisibility(View.INVISIBLE);


        mSourceText.setDrawingCacheEnabled(false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        Log.d("CIRCLE", "TouchEvent");
        int pointerID;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_POINTER_UP:
                //Log.d("touch","action pointer up");

                Log.d("CIRCLE", "TouchEvent up");

                break;
            case MotionEvent.ACTION_UP:

                mcircleCenterX = event.getX();
                mcircleCenterY = event.getY();
                prepareWipe();
                makeCircleAnimation(event.getX());
                break;

            case MotionEvent.ACTION_CANCEL:
                //Log.d("touch","action cancel");

                break;

            case MotionEvent.ACTION_MOVE:
                //Log.d("touch","action move");

                break;

            default:
                break;

        }
        return false;
    }

    private void makeCircleAnimation(float touchX) {

        ValueAnimator peepHoleAnim = ValueAnimator.ofFloat();
        peepHoleAnim.setInterpolator(new LinearInterpolator());


        //if(touchX<mWidth/2){
        //    peepHoleAnim.setFloatValues(0, mWidth - touchX);
        //}else {
        //    peepHoleAnim.setFloatValues(0,touchX);
        //}

        Double distances[] = new Double[4];
        distances[0]=dist(mcircleCenterX,mcircleCenterY,0,0);
        distances[1]=dist(mcircleCenterX,mcircleCenterY,0,mHeight);
        distances[2]=dist(mcircleCenterX,mcircleCenterY,mWidth,0);
        distances[3]=dist(mcircleCenterX,mcircleCenterY,mWidth,mHeight);

        peepHoleAnim.setFloatValues(0,(float)greatest(distances));

        peepHoleAnim.setDuration((long) (mTimetoCrossWidth*(greatest(distances)/mWidth)));


        peepHoleAnim.addListener(mAnimlistener);

        peepHoleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Float animatedValue = (Float) animation.getAnimatedValue();

                Canvas revealCanvas = new Canvas(mBitmap);

                Paint myPaint = new Paint();
                myPaint.setColor(Color.TRANSPARENT);
                myPaint.setStyle(Paint.Style.FILL);

                myPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


                revealCanvas.drawCircle(mcircleCenterX,mcircleCenterY,animatedValue,myPaint);
                //revealCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mDestinationText.invalidate();


            }
        });

        peepHoleAnim.start();


    }

    private double greatest(Double[] distances) {

        Double greatest=0.0;

        for(int x=0;x<distances.length;x++){
            if(distances[x]>greatest)greatest=distances[x];
        }
        return greatest;

    }


    double dist(float x1, float y1, float x2, float y2){
         return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1- y2, 2));
    }





}





