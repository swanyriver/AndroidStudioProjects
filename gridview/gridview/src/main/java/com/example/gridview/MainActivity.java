package com.example.gridview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private circleFrame myCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         myCircle = new circleFrame(this);
        myCircle.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP));
        //myCircle.setBackgroundColor(Color.BLUE);
        myCircle.setImageResource(R.drawable.heart);

        ((FrameLayout)findViewById(R.id.myframe)).addView(myCircle);
        //myCircle.invalidate();

    }



    class circleFrame extends ImageView{

        private boolean circledrawn = false;

        private float mTranslation = 0;
        private boolean mTranslating=false;
        private int height;
        private int width;
        private Paint mypaint;


        public circleFrame(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {




            if(!circledrawn){
                height = canvas.getHeight();
                width = canvas.getWidth();
                mypaint = new Paint();
                mypaint.setStyle(Paint.Style.FILL);
                mypaint.setColor(Color.RED);


                //canvas.drawColor(Color.RED, PorterDuff.Mode.CLEAR);
                //canvas.drawCircle(width/2,height/2,height/2,mypaint);



                circledrawn=true;

            }



            super.onDraw(canvas);
        }

        @Override
        public void draw(Canvas canvas) {

            if(mTranslating){
                //canvas.clipRect(new Rect(0,0,width,height*3));
                canvas.scale(3f,3f);
                canvas.translate(0,mTranslation);


                //canvas.drawCircle(width/2,height/2,height/2,mypaint);
            }


            super.draw(canvas);
        }

        public void translate(){
           ValueAnimator translate = ValueAnimator.ofFloat(0,getHeight()*3);
           translate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
               @Override
               public void onAnimationUpdate(ValueAnimator animation) {
                   mTranslation = (Float) animation.getAnimatedValue();
                   //Log.d("translate", "translating "+ (Float) animation.getAnimatedValue());
                   invalidate();
               }
           });
           translate.setDuration(900);
           mTranslating=true;
           translate.start();
       }
    }


    public void myButton (View view){
        myCircle.translate();
    }

}

