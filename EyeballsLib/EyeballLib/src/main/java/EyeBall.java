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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class EyeBall extends ImageView implements EyeBallINT{


    private final int mOffsetforPupil;
    private int mHeight;
    private int mWidth;
    private int[] mLocationinWindow = new int[2];




    private int mEyeballCenterWindowX;
    private int mEyeballCenterWindowY;
    private int mEyeballCenterLocalX;
    private int mEyeballCenterLocalY;

    private Path mCirclePath;
    private float mRadius;
    private int mPupilCenterX;
    private int mPupilCenterY;



    private Paint mPaint = new Paint();

    protected long RUBBERINESS = 600;
    protected long DELAY = 60;
    protected long EYEADJUSTDURATION = 200;
    protected long EYEADJUSTDELAY = 0;
    private DecelerateInterpolator mdecellINTER = new DecelerateInterpolator();
    private OvershootInterpolator mOvershootINTER = new OvershootInterpolator();

    private int myState = 0;
    static final int NOFOCUS = 0;
    static final int FOCUSED = 1;
    static final int FOCUSING = 2;
    public static final int PLEASE_LOOSE_FOCUS = 101;

    private int FOCUSX;
    private int FOCUSY;
    private AnimatorSet mEyeToFocusAnimationSet = new AnimatorSet();


    private Bitmap mPupilBM;
    private ValueAnimator.AnimatorUpdateListener mInvalidateListener;



    public EyeBall(Context context, int width, int height, EyeDraw eyeDraw) {
        super(context);

        post(new Runnable() {
            @Override
            public void run() {
                findCenterInWindow();


            }
        });

        mInvalidateListener = new invalidateOnUpdate();


        ////////////////////////////////
        ////////Measuring
        ///////////////////////////////
        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mHeight = width;
        mWidth = height;
        mEyeballCenterLocalX = mWidth / 2;
        mEyeballCenterLocalY = mHeight/2;


        int eyeheight = (int) (mHeight * eyeDraw.percent_eye_white_vertical);
        int eyeWidth = (int) (mWidth * eyeDraw.percent_eye_white_horizantal);
        if(eyeWidth>eyeheight)mRadius=eyeheight/2;
        else mRadius=eyeWidth/2;


        float ovalOffsetVertical = (mHeight-eyeheight)/2;
        float ovalOffsetHorizantal = (mWidth-eyeWidth)/2;
        RectF ovalRect = new RectF(0+ovalOffsetHorizantal,0+ovalOffsetVertical,mWidth-ovalOffsetHorizantal,mHeight-ovalOffsetVertical);


        ////////////////////////////////
        ////////create pupil bitmap
        ///////////////////////////////
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inMutable = true;
        //mPupilBM = BitmapFactory.decodeResource(getResources(),R.drawable.pupil, myOptions);
        mPupilBM = BitmapFactory.decodeResource(getResources(),eyeDraw.pupil_resource_id, myOptions);
        mPupilBM = Bitmap.createScaledBitmap(mPupilBM, (int) (mRadius*2*eyeDraw.pupil_size), (int) (mRadius*2*eyeDraw.pupil_size), false);
        mOffsetforPupil = (mPupilBM.getWidth()/2)*-1;


        ////////////////////////////////
        ////////draw black border (full oval)
        ///////////////////////////////
        myOptions.inMutable = true;
        myOptions.outHeight=mHeight;
        myOptions.outWidth=mWidth;
        //Bitmap circleBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Bitmap circleBitmap = BitmapFactory.decodeResource(getResources(),eyeDraw.background_resource_id, myOptions);
        circleBitmap = Bitmap.createScaledBitmap(circleBitmap, mWidth, mHeight, false);
        Canvas circleCanvas = new Canvas(circleBitmap);
        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setAntiAlias(true);

        mCirclePath = new Path();

        mCirclePath.addOval(ovalRect, Path.Direction.CW);
        mCirclePath.close();

        circleCanvas.drawPath(mCirclePath, circlePaint);



        ////////////////////////////////
        ////////draw white innercicle and create clippath
        ///////////////////////////////
        float margin = (float) (mRadius*.05);
        ovalRect.top+=margin;
        ovalRect.bottom-=margin;
        ovalRect.right-=margin;
        ovalRect.left+=margin;

        mCirclePath.reset();
        mCirclePath.addOval(ovalRect, Path.Direction.CW);
        mCirclePath.close();

        circlePaint.setColor(Color.WHITE);
        circleCanvas.drawPath(mCirclePath, circlePaint);
        setImageBitmap(circleBitmap);  //this is the returned bitmap

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        mPupilCenterX = mWidth/2;
        mPupilCenterY = mHeight/2;


        invalidate();


    }

    public void focusHere(int x, int y){

        FOCUSX=x;
        FOCUSY=y;

        if(myState==FOCUSED){
            //int newPupilCords[] = getPupilCoords();
            //check for extreme difference, animate

            //if(distanceToNewPupilCenter(newPupilCords)>7){
            //  getAttention(EYEADJUSTDURATION, EYEADJUSTDELAY);
            //  return;
            //}

            invalidate();
            return;

            //setPupilCenterX(newPupilCords[0]);
            //setPupilCenterY(newPupilCords[1]);
        }else if(myState==NOFOCUS) {
            mEyeToFocusAnimationSet.cancel();
            getAttention(RUBBERINESS,DELAY);
        }



    }



    public void focusHere(int RequestCode){
        if(RequestCode==PLEASE_LOOSE_FOCUS){


            mEyeToFocusAnimationSet.cancel();
            myState=NOFOCUS;
            returnEyetoCenter();
        }
    }

    public double distanceToNewPupilCenter(int newCords[]){
        int dx=mPupilCenterX-newCords[0];
        int dy =mPupilCenterY - newCords[1];
        return Math.sqrt(dx * dx + dy * dy);
    }



    //changed to public for line creation, if find another way change it back
    public void findCenterInWindow() {
        getLocationInWindow(mLocationinWindow);
        mEyeballCenterWindowX =mLocationinWindow[0]+mWidth/2;
        mEyeballCenterWindowY =mLocationinWindow[1]+mHeight/2;

        //Log.d("Center", "find CenterinWindow  X:" + mEyeballCenterWindowX + " Y:" + mEyeballCenterWindowY);

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

    static  float getDistancetoNonSQRTD(int x, int y, int x2, int y2){
        int dx = x - x2;
        int dy = y - y2;
        return dx * dx + dy * dy;
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
        eyeToCenter.addUpdateListener(mInvalidateListener);

        eyeToCenter.start();

    }

    private void getAttention(long duration, long delay){

        int pupilCoords[] = getPupilCoords();


        ObjectAnimator pupilX = ObjectAnimator.ofInt(this,"PupilCenterX", pupilCoords[0]);
        ObjectAnimator pupilY = ObjectAnimator.ofInt(this,"PupilCenterY", pupilCoords[1]);


        pupilX.addUpdateListener(new getAttentionUpdate(true,getPupilCenterX()));
        pupilY.addUpdateListener(new getAttentionUpdate(false,getPupilCenterY()));

        pupilX.addUpdateListener(mInvalidateListener);
        pupilY.addUpdateListener(mInvalidateListener);

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


    private class getAttentionUpdate implements ValueAnimator.AnimatorUpdateListener{

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

    class invalidateOnUpdate implements ValueAnimator.AnimatorUpdateListener{

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(myState==FOCUSED){
            int newPupilCords[] = getPupilCoords();
            if(distanceToNewPupilCenter(newPupilCords)>7){
                getAttention(EYEADJUSTDURATION, EYEADJUSTDELAY);

            }
            else{
                setPupilCenterX(newPupilCords[0]);
                setPupilCenterY(newPupilCords[1]);
            }

        }
        canvas.clipPath(mCirclePath);
        canvas.drawBitmap(mPupilBM,mPupilCenterX+mOffsetforPupil,mPupilCenterY+mOffsetforPupil,mPaint);


    }



    public int getPupilCenterX(){
        return mPupilCenterX;
    }

    public void setPupilCenterX(int x){
        mPupilCenterX=x;
        //invalidate();
    }
    public int getPupilCenterY(){
        return mPupilCenterY;
    }

    public void setPupilCenterY(int Y){
        mPupilCenterY=Y;
        //invalidate();
    }

    public int getmEyeballCenterWindowX() {return mEyeballCenterWindowX; }

    public int getmEyeballCenterWindowY() { return mEyeballCenterWindowY; }


}
