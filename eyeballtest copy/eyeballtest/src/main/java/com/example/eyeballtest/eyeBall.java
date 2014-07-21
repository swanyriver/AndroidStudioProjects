package com.example.eyeballtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

/**
 * Created by Brandon on 11/29/13.
 */
public class eyeBall extends ImageView{


    private final int mOffsetforPupil;
    private int mHeight;
    private int mWidth;
    private int[] mLocationinWindow = new int[2];
    //private int mEyeballCenterWindowX;
    //private int mEyeballCenterWindowY;
    //private int mEyeballCenterLocalX;
    //private int mEyeballCenterLocalY;
    //for joined experiment
    public int mEyeballCenterWindowX;
    public int mEyeballCenterWindowY;
    public int mEyeballCenterLocalX;
    public int mEyeballCenterLocalY;

    private Path mCirclePath;
    private float mRadius;
    private int mPupilCenterX;
    private int mPupilCenterY;

    private float mPupilRadius;
    private Paint mPupilPaint;

    private long RUBBERINESS = 600;
    private long DELAY = 60;
    private long EYEADJUSTDURATION = 600;
    private long EYEADJUSTDELAY = 0;
    private DecelerateInterpolator mdecellINTER = new DecelerateInterpolator();
    private OvershootInterpolator mOvershootINTER = new OvershootInterpolator();

    private int myState = 0;
    static final int NOFOCUS = 0;
    static final int FOCUSED = 1;
    static final int FOCUSING = 2;
    static final int PLEASE_LOOSE_FOCUS = 101;

    private int FOCUSX;
    private int FOCUSY;
    private AnimatorSet mEyeToFocusAnimationSet;


    private Bitmap mPupilBM;




    public eyeBall(Context context, int width, int height) {
        super(context);

        post(new Runnable() {
            @Override
            public void run() {
                findCenterInWindow();

            }
        });


        mHeight = width;
        mWidth = height;

        if(mWidth>mHeight)mRadius=mHeight/2;
        else mRadius=mWidth/2;


        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inMutable = true;
        //myOptions.outHeight= (int) (mRadius/3*2);
       // myOptions.outWidth=mWidth/3*2;

        mPupilBM = BitmapFactory.decodeResource(getResources(),R.drawable.pupil, myOptions);
        mPupilBM = Bitmap.createScaledBitmap(mPupilBM, (int) (mRadius*2/8*7), (int) (mRadius*2/8*7), false);

        mOffsetforPupil = (mPupilBM.getWidth()/2)*-1;


        mEyeballCenterLocalX = mWidth / 2;
        mEyeballCenterLocalY = mHeight/2;








        //BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inMutable = true;
        myOptions.outHeight=mHeight;
        myOptions.outWidth=mWidth;

        //create 2 Bitmaps, exact size as image view
        Bitmap circleBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        Canvas circleCanvas = new Canvas(circleBitmap);

        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.BLACK);


        mCirclePath = new Path();
        //mCirclePath.addCircle(mEyeballCenterLocalX, mEyeballCenterLocalY, mRadius, Path.Direction.CW);
        RectF ovalRect = new RectF(0,0,mWidth,mHeight);
        mCirclePath.addOval(ovalRect, Path.Direction.CW);
        mCirclePath.close();

        circleCanvas.drawPath(mCirclePath, circlePaint);


        circlePaint.setColor(Color.WHITE);

        mCirclePath.reset();

        //mCirclePath.addCircle(mEyeballCenterLocalX, mEyeballCenterLocalY, mRadius-10, Path.Direction.CW);

        float margin = (float) (mRadius*.05);
        ovalRect.top+=margin;
        ovalRect.bottom-=margin;
        ovalRect.right-=margin;
        ovalRect.left+=margin;
        mCirclePath.addOval(ovalRect, Path.Direction.CW);
        mCirclePath.close();

        circleCanvas.drawPath(mCirclePath, circlePaint);
        setImageBitmap(circleBitmap);



       //pupil stuff
        mPupilRadius = mRadius/2;

        mPupilCenterX = mWidth/2;
        mPupilCenterY = mHeight/2;

        mPupilPaint = new Paint();

        mPupilPaint.setStyle(Paint.Style.FILL);
        mPupilPaint.setColor(Color.BLACK);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        invalidate();

        getPupilCenterX();
        setPupilCenterX(mPupilCenterX);
        getPupilCenterY();
        setPupilCenterY(mPupilCenterY);



    }

    public void focusHere(int x, int y){

        FOCUSX=x;
        FOCUSY=y;

        if(myState==FOCUSED){
            int newPupilCords[] = getPupilCoords();
            //check for extreme difference, animate

            if(distanceToNewPupilCenter(newPupilCords)>7){
                getAttention(EYEADJUSTDURATION, EYEADJUSTDELAY);
                return;
            }


            setPupilCenterX(newPupilCords[0]);
            setPupilCenterY(newPupilCords[1]);
        }else if(myState==NOFOCUS) {
            if(mEyeToFocusAnimationSet!=null)mEyeToFocusAnimationSet.cancel();
            getAttention(RUBBERINESS,DELAY);
        }



    }

    public double distanceToNewPupilCenter(int newCords[]){
            int dx=mPupilCenterX-newCords[0];
            int dy =mPupilCenterY - newCords[1];
            return Math.sqrt(dx * dx + dy * dy);
    }

    public void focusHere(int RequestCode){
        if(RequestCode==PLEASE_LOOSE_FOCUS){

            mEyeToFocusAnimationSet.cancel();
            myState=NOFOCUS;
            returnEyetoCenter();
        }
    }

    private void findCenterInWindow() {
        getLocationInWindow(mLocationinWindow);
        mEyeballCenterWindowX =mLocationinWindow[0]+mWidth/2;
        mEyeballCenterWindowY =mLocationinWindow[1]+mHeight/2;
    }

    private void returnEyetoCenter() {

        PropertyValuesHolder x = PropertyValuesHolder.ofInt("PupilCenterX", mEyeballCenterLocalX);
        PropertyValuesHolder y = PropertyValuesHolder.ofInt("PupilCenterY", mEyeballCenterLocalY);

        ObjectAnimator eyeToCenter = ObjectAnimator.ofPropertyValuesHolder(this,x,y);
        eyeToCenter.setDuration(RUBBERINESS);
        eyeToCenter.setStartDelay(DELAY);
        eyeToCenter.setInterpolator(mOvershootINTER);


        eyeToCenter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (myState != NOFOCUS) animation.cancel();

            }
        });

        eyeToCenter.start();

    }

    private void getAttention(long duration, long delay){

        int pupilCoords[] = getPupilCoords();


        ObjectAnimator pupilX = ObjectAnimator.ofInt(this,"PupilCenterX", pupilCoords[0]);
        ObjectAnimator pupilY = ObjectAnimator.ofInt(this,"PupilCenterY", pupilCoords[1]);


        pupilX.addUpdateListener(new getAttentionUpdate(true,getPupilCenterX()));
        pupilY.addUpdateListener(new getAttentionUpdate(false,getPupilCenterY()));

        mEyeToFocusAnimationSet = new AnimatorSet();

        mEyeToFocusAnimationSet.play(pupilX).with(pupilY);

        mEyeToFocusAnimationSet.setDuration(duration);
        mEyeToFocusAnimationSet.setStartDelay(delay);
        mEyeToFocusAnimationSet.setInterpolator(mdecellINTER);



        mEyeToFocusAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                //Log.d("anim","focus canceled");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myState = FOCUSED;
                //Log.d("anim","focus ended");
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                myState = FOCUSING;
            }
        });


        mEyeToFocusAnimationSet.start();

    }


    class getAttentionUpdate implements ValueAnimator.AnimatorUpdateListener{

        private int coordIndex;
        private int startValue;

        getAttentionUpdate(boolean isX, int start){
            startValue=start;
            if(isX)coordIndex=0;
            else coordIndex=1;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int newCoords[]=getPupilCoords();

            animation.setIntValues(startValue,newCoords[coordIndex]);
        }
    }





    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.clipPath(mCirclePath);

        //black circle draw
        //canvas.drawCircle(mPupilCenterX,mPupilCenterY,mPupilRadius,mPupilPaint);

        //image draw
        canvas.drawBitmap(mPupilBM,mPupilCenterX+mOffsetforPupil,mPupilCenterY+mOffsetforPupil,new Paint());

    }

    public int getPupilCenterX(){
        return mPupilCenterX;
    }

    public void setPupilCenterX(int x){
        mPupilCenterX=x;
        invalidate();
    }
    public int getPupilCenterY(){
        return mPupilCenterY;
    }

    public void setPupilCenterY(int Y){
        mPupilCenterY=Y;
        invalidate();
    }


    private int[] getPupilCoords(){

        findCenterInWindow();

        int startX = mEyeballCenterWindowX;
        int startY = mEyeballCenterWindowY;
        int dx = FOCUSX - startX;
        int dy = FOCUSY - startY;
        float distToTarget = (float) Math.sqrt(dx * dx + dy * dy);
        float ratio = (float)(mRadius*.60) / distToTarget;
        int endX = mEyeballCenterLocalX + Math.round(ratio * dx);
        int endY = mEyeballCenterLocalY + Math.round(ratio * dy);

        //Log.d("pupilCoords", "CENTER WINDOW = X:" + mEyeballCenterWindowX + " Y:" + mEyeballCenterWindowY + "  touch=X:" + FOCUSX + " Y:" + FOCUSY);

        return new int[]{endX,endY};



    }

    public  float getDistancetoTouch(int x, int y){
        int dx = x - mEyeballCenterWindowX;
        int dy = y - mEyeballCenterWindowY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
