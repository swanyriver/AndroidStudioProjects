package com.example.eyeballdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import EyeBalls.EyeBall;
import EyeBalls.EyeBrain;
import EyeBalls.EyeBrainAdapter;
import EyeBalls.EyeDrawables;
import EyeBalls.EyeFocus;
import EyeBalls.EyeGroup;
import EyeBalls.EyeSet;
import EyeBalls.StupidEyeTouchListner;

public class MainActivity extends Activity {

    EyeGroup mEyeGroup = new EyeGroup();
    FrameLayout mEyeFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEyeFrame = (FrameLayout) findViewById(R.id.container);

        //Bitmap testBitmap = EyeDrawables.drawEye(this,200,200,1);
        //EyeDrawables.getEyeDraw(0,this);
        //((ImageView) findViewById(R.id.myImage)).setImageBitmap();

        mEyeFrame.post(new Runnable() {
            @Override
            public void run() {
                int width = mEyeFrame.getWidth();
                int height = mEyeFrame.getHeight();
                int ninthwidth = width/9;
                int sixheight = height/6;

                EyeSet myEyeSetOne = new EyeSet(mEyeFrame);
                myEyeSetOne.addEyeball(ninthwidth,ninthwidth,ninthwidth,sixheight*2, EyeDrawables.getEyeDraw(0, getApplicationContext()));
                myEyeSetOne.addEyeball(ninthwidth,ninthwidth,ninthwidth*2+ninthwidth/2,sixheight*2,EyeDrawables.getEyeDraw(0,getApplicationContext()));

                EyeSet myEyeSetTwo = new EyeSet(mEyeFrame);
                myEyeSetTwo.addEyeball((int) (ninthwidth*1.5),ninthwidth,width-ninthwidth,sixheight*4,EyeDrawables.getEyeDraw(1,getApplicationContext()));
                myEyeSetTwo.addEyeball(ninthwidth, (int) (ninthwidth*1.5),width-(ninthwidth*2+ninthwidth/2),sixheight*4,EyeDrawables.getEyeDraw(1,getApplicationContext()));



                mEyeGroup.addEyeset(myEyeSetOne);
                mEyeGroup.addEyeset(myEyeSetTwo);

                EyeBrain myBrain = new EyeBrainAdapter(mEyeGroup){
                    @Override
                    public void focusHere(int focusRequestX, int focusRequestY) {
                        //super.focusHere(focusRequestX, focusRequestY);
                        final EyeFocus myGroup = sendMessage();
                        final int x=focusRequestX;
                        final int y=focusRequestY;
                        mEyeFrame.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                myGroup.focusHere(x,y);
                            }
                        },400);
                    }
                };


                //mEyeFrame.setOnTouchListener(new StupidEyeTouchListner(mEyeGroup));
                mEyeFrame.setOnTouchListener(new StupidEyeTouchListner(myBrain));
                mEyeFrame.setClickable(true);
            }
        });








    }



}
