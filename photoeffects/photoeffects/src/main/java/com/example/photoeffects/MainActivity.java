package com.example.photoeffects;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends Activity {


    private funnyImage myImage;
    private Button ourButton;
    private SeekBar ourSeekBar;
    private SeekBar widthSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myImage = new funnyImage(this);
        myImage.setImageResource(R.drawable.dorisheadsmall);
        myImage.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        ourButton = (Button) findViewById(R.id.mybutton);
        ourButton.setText("lets do it");

        widthSeekBar = (SeekBar) findViewById(R.id.widthseekbar);
        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myImage.setREVEALWIDTH(progress);
                myImage.getrevealValue(ourSeekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ourSeekBar = (SeekBar) findViewById(R.id.revealwindowseekbar);
        ourSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                myImage.getrevealValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });






        ((FrameLayout) findViewById(R.id.container)).addView(myImage);

    }

    public void myButton(View view){

        myImage.RevealAnimation();

    }



}

class funnyImage extends ImageView{

    private int DIVISOR = 15;
    private Paint myPaint = new Paint();
    private Path linePath = new Path();
    private Path mRevealPath = new Path();
    private int myHeight;
    private int myWidth;

    private int REVEALWIDTH = 250;
    private PointF revealTopLeft = new PointF(0,0);
    private PointF revealTopRight = new PointF(0,0);
    private PointF revealBottomLeft = new PointF(0,0);
    private PointF revealBottomRight = new PointF(0,0);


    private boolean GRID_LINE_ANIMATION=false;
    private boolean WINDOW_ANIMATION=false;

    private Bitmap myBitmap;


    public funnyImage(Context context) {
        super(context);


        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        myPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        myPaint.setColor(Color.TRANSPARENT);
        myPaint.setStrokeWidth(3f);
        myPaint.setStyle(Paint.Style.STROKE);

        myBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.dorisheadsmall);


        post(new Runnable() {
            @Override
            public void run() {
                makeGridLines();

            }
        });


    }

    public void setREVEALWIDTH(int percent){
        //REVEALWIDTH =width;
        REVEALWIDTH = (int) ((myHeight+myWidth+ REVEALWIDTH)*((float)percent/100));

    }

    private void makeGridLines() {
        myHeight = getHeight();
        myWidth = getWidth();
        float HeightDiviseAmount = myHeight/DIVISOR;
        float WidthDiviseAmount = myWidth/DIVISOR;

        for(int x=1;x<DIVISOR;x++){
            //downlines
            linePath.moveTo(x*WidthDiviseAmount,0);
            linePath.lineTo(x*WidthDiviseAmount,myHeight);
            //sidewayslines
            linePath.moveTo(0,x*HeightDiviseAmount);
            linePath.lineTo(myWidth,x*HeightDiviseAmount);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(GRID_LINE_ANIMATION){
            canvas.clipPath(mRevealPath, Region.Op.INTERSECT);
            canvas.drawPath(linePath, myPaint);

        }
        if(WINDOW_ANIMATION){
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.clipPath(mRevealPath, Region.Op.INTERSECT);
            //canvas.drawColor(Color.RED);

            canvas.drawBitmap(myBitmap,0,0,new Paint());

        }
        canvas.drawPath(mRevealPath,myPaint);

    }

    public void RevealAnimation(){

        //todo put bool flag Grid_Line_animation here

        ValueAnimator revealPathAnim = ValueAnimator.ofFloat(myHeight+myWidth+ REVEALWIDTH);
        revealPathAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();

                repositionRevealWindow(value);



            }
        });


        revealPathAnim.setDuration(1200*2);
        revealPathAnim.start();
    }

    public void getrevealValue(int percent){
        repositionRevealWindow((myHeight+myWidth+ REVEALWIDTH)*((float)percent/100));

    }

    private void repositionRevealWindow(float value) {


        movepointsRightThenDown(revealTopRight,value);
        movepointsRightThenDown(revealTopLeft,value- REVEALWIDTH);
        movepointsDownthenRight(revealBottomRight,value);
        movepointsDownthenRight(revealBottomLeft,value- REVEALWIDTH);





        mRevealPath.reset();
        mRevealPath.moveTo(revealTopLeft.x, revealTopLeft.y);
        if(revealTopRight.y!=0)mRevealPath.lineTo(myWidth,0);
        mRevealPath.lineTo(revealTopRight.x, revealTopRight.y);
        mRevealPath.lineTo(revealBottomRight.x, revealBottomRight.y);
        if(revealBottomRight.x!=0)mRevealPath.lineTo(0,myHeight);
        mRevealPath.lineTo(revealBottomLeft.x, revealBottomLeft.y);
        mRevealPath.lineTo(revealTopLeft.x,revealTopLeft.y);
        mRevealPath.close();

        /*mRevealPath.reset();
        mRevealPath.addCircle(revealTopLeft.x, revealTopLeft.y, 15, Path.Direction.CCW);
        mRevealPath.addCircle(revealTopRight.x, revealTopRight.y, 15, Path.Direction.CCW);
        mRevealPath.addCircle(revealBottomLeft.x, revealBottomLeft.y, 15, Path.Direction.CCW);
        mRevealPath.addCircle(revealBottomRight.x,revealBottomRight.y,15, Path.Direction.CCW);
        mRevealPath.close();*/


        WINDOW_ANIMATION=true;
        invalidate();
    }

    private void movepointsRightThenDown(PointF revealPoint, float pointOnTraverse){
        if(pointOnTraverse<0){
            revealPoint.x=0;revealPoint.y=0;
            return;
        }

        if(pointOnTraverse>myHeight+myWidth){
            revealPoint.x=myWidth;revealPoint.y=myHeight;
            return;
        }
        if(pointOnTraverse<myWidth){
            revealPoint.x=pointOnTraverse;
            revealPoint.y=0;
        }else{
            revealPoint.x=myWidth;
            revealPoint.y=pointOnTraverse-myWidth;
        }

    }
    private void movepointsDownthenRight(PointF revealPoint, float pointOnTraverse){
        if(pointOnTraverse<0){
            revealPoint.x=0;revealPoint.y=0;
            return;
        }

        if(pointOnTraverse>myHeight+myWidth){
            revealPoint.x=myWidth;revealPoint.y=myHeight;
            return;
        }
        if(pointOnTraverse<myHeight){
            revealPoint.y=pointOnTraverse;
            revealPoint.x=0;
        }else{
            revealPoint.y=myHeight;
            revealPoint.x=pointOnTraverse-myHeight;
        }

    }

}
