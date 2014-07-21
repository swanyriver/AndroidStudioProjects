package com.example.eyeballtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;

import EyeBalls.EyeBall;
import EyeBalls.EyeBrain;
import EyeBalls.EyeDrawables;
import EyeBalls.EyeFocus;
import EyeBalls.EyeFocusBrainInterface;
import EyeBalls.EyeGroup;
import EyeBalls.EyeSet;
import EyeBalls.ProximityBrain;
import EyeBalls.SmartEyeBrainwithIDs;

public class eyeMainActivity extends Activity {


    private FrameLayout mEyeFrameContainer;
    private EyeFrame mEyeFrame;


    //cameraStuff
    private static int REQUEST_CODE=1;
    private Bitmap bitmap;
    private EyeGroup mEyeGroup;
    private EyeBrain mEyeBrain;

    static final int EYEBALL_WIDTH = 100;
    static final int EYEBALL_HEIGHT = 100;

    private int mEyedrawIndex =0;
    private ImageView mEyePreview;
    private ObjectAnimator mFadeOut;
    private ImageButton mAddEyeButton;
    private boolean ADDING_EYES = true;
    private ValueAnimator mHiglightButtonAnim;
    private Bitmap animBitmapAddEyeButton;
    private Paint mHighlightPaint = new Paint();
    private Path mHighlightPath = new Path();
    private Canvas mHighlightCanvas;
    private boolean TEST_VIZUALIZERS=true;
    //private boolean mProxBrainOn=false;

    private View.OnTouchListener myEyePlacingTouch = new eyePlace();
    private View.OnTouchListener myGroupFocusTouch = new fingerTouch();
    private View.OnTouchListener myStupidBrainFocusTouch = new StupidBrainTouch();
    private View.OnTouchListener mySmartBrainFocusTouch;

    private View.OnTouchListener myFocusTouchListner=  myGroupFocusTouch;
    private ToggleButton mBrainButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_main_layout);


        mEyeFrameContainer = (FrameLayout) findViewById(R.id.container);
        mEyeFrame = new EyeFrame(this);
        mEyeFrame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mEyeFrameContainer.addView(mEyeFrame,1);

        mBrainButton = (ToggleButton) findViewById(R.id.brainon);



        mEyeFrame.setClickable(true);
        //findViewById(R.id.container).setOnTouchListener(new fingerTouch());


        mEyeFrame.setOnTouchListener(myEyePlacingTouch);


        mEyePreview = (ImageView) findViewById(R.id.displayeye);
        makeHighlightAnim();

        mHiglightButtonAnim.start();



    }

    public void vizButton(View view){
        if(TEST_VIZUALIZERS){
            for(int index = 0; index<mEyeGroup.getmNumEyesets();index++)mEyeGroup.setVizualizationOn(index,false);

            findViewById(R.id.controlFrame).setVisibility(View.INVISIBLE);
            TEST_VIZUALIZERS=false;

        }else{
            for(int index = 0; index<mEyeGroup.getmNumEyesets();index++)mEyeGroup.setVizualizationOn(index,true);
            findViewById(R.id.controlFrame).setVisibility(View.VISIBLE);
            TEST_VIZUALIZERS=true;
        }
        mEyeFrame.invalidate();
    }

    class EyeFrame extends FrameLayout{
        EyeFrame(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if(mEyeGroup !=null){
                for(int index = 0; index<mEyeGroup.getmNumEyesets();index++){
                    if(mEyeGroup.getVizualizationOn(index)){
                        super.onDraw(canvas);
                        canvas.drawPath(mEyeGroup.getvizBorderPath(index), mEyeGroup.getvizPaint(index));
                        canvas.drawPath(mEyeGroup.getvizPath(index), mEyeGroup.getvizPaint(index));
                        mEyeGroup.resetVizPath(index);
                    }
                }
            }
        }
    }





    public void makeDummyEyeset(View view){

        view.setVisibility(View.INVISIBLE);

        //mEyeFrame.removeAllViews();
        //mEyeGroup = new EyeGroup();

        //todo add two or three sets of eyes with differnt drawables

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


        if(mEyeGroup==null) mEyeGroup=new EyeGroup();
        mEyeGroup.addEyeset(myEyeSetOne);
        mEyeGroup.addEyeset(myEyeSetTwo);

        findViewById(R.id.setButton).setEnabled(true);
        if(!((ToggleButton) findViewById(R.id.brainon)).isChecked())myFocusTouchListner=myGroupFocusTouch;
        else if (((RadioButton) findViewById(R.id.brainprox)).isChecked()) myFocusTouchListner=myStupidBrainFocusTouch;
        else myFocusTouchListner=mySmartBrainFocusTouch;




    }

    public void brainOne (View view){

        ////////////////////////
        //// "I LIKE NEW THINGS"
        ////////////////////////


        class ILikeNewThingsSmartBrain extends SmartEyeBrainwithIDs {

            private LinkedList<EyeBrainID> IDstack = new LinkedList<EyeBrainID>();

            public ILikeNewThingsSmartBrain(EyeFocus eyeFocus) {
                super(eyeFocus);
                IDstack.add(EyeBrainID.BLANK_ID);
            }

            @Override
            public EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, EyeBrainID requestID) {
                super.thinkFocusHere(focusRequestX, focusRequestY, requestID);

                Log.d("smart", "focusrequest from " + requestID.toString());

                if(requestID.equals(IDstack.getLast())) sendMessage().focusHere(focusRequestX,focusRequestY);

                if(!IDstack.contains(requestID))return new EyeBrainID(requestID.getID(),EyeBrainID.NO_LONGER_FOCUSED);

                else return new EyeBrainID(requestID.getID(),EyeBrainID.NO_RESPONSE);
            }

            @Override
            protected void pleaseLooseFocus(EyeBrainID requestID) {
                if(IDstack.contains(requestID))IDstack.remove(requestID);
                if(IDstack.size()==1)sendMessage().request(EyeBall.PLEASE_LOOSE_FOCUS);

            }

            @Override
            protected void onIDIssued(EyeBrainID issuedID) {
                IDstack.add(issuedID);

            }
        }

        mEyeBrain=new ILikeNewThingsSmartBrain(mEyeGroup);
        mEyeFrame.setOnTouchListener(new SmartEyeBrainwithIDs.SmartBrainWithIDTouchListenerAdapter(mEyeBrain){
            @Override
            protected void focusPointerUp(int x, int y, EyeFocusBrainInterface.EyeBrainID currentEyeID) {
                //super.focusPointerUp(x, y, currentEyeID);
                mTouchEyeBrain.thinkRequest(EyeBall.PLEASE_LOOSE_FOCUS, currentEyeID);
            }

            @Override
            protected void focusAllFingersUp(int x, int y, EyeFocusBrainInterface.EyeBrainID currentEyeID) {
                //super.focusAllFingersUp(x, y, currentEyeID);
                mTouchEyeBrain.thinkRequest(EyeBall.PLEASE_LOOSE_FOCUS, currentEyeID);
            }
        });

    }
    public void brainTwo (View view){

    }

    public void proxButton(View view){

        mEyeBrain=new ProximityBrain(mEyeGroup);

    }

    public void brainOn(View view){

        if(mEyeBrain==null){
            mEyeBrain=new ProximityBrain(mEyeGroup);
        }


        if(((ToggleButton) view).isChecked()){
            if (((RadioButton) findViewById(R.id.brainprox)).isChecked()) myFocusTouchListner=myStupidBrainFocusTouch;
            else myFocusTouchListner=mySmartBrainFocusTouch;



        }else myFocusTouchListner=myGroupFocusTouch;

        mEyeFrame.setOnTouchListener(myFocusTouchListner);
    }

    private void makeHighlightAnim() {
        mFadeOut = ObjectAnimator.ofFloat(mEyePreview, "Alpha", 0f);
        mFadeOut.setStartDelay(1500);


        mAddEyeButton = (ImageButton) findViewById(R.id.myButton);
        setEyeButtonImage();
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(7);
        mHighlightPaint.setColor(Color.GREEN);


        mHighlightPath.moveTo(0,0);
        mHighlightPath.lineTo(50, 0);
        mHighlightPath.lineTo(50, 50);
        mHighlightPath.lineTo(0, 50);
        mHighlightPath.lineTo(0,0);
        mHighlightPath.close();

        mHiglightButtonAnim = ValueAnimator.ofInt(0, 255);
        mHiglightButtonAnim.setRepeatCount(ValueAnimator.INFINITE);
        mHiglightButtonAnim.setRepeatMode(ValueAnimator.REVERSE);
        mHiglightButtonAnim.setDuration(500);

        mHiglightButtonAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animBitmapAddEyeButton = EyeDrawables.drawEye(getApplicationContext(), 50, 50, mEyedrawIndex);
                //animBitmapAddEyeButton.setHasAlpha(true);
                mHighlightCanvas = new Canvas(animBitmapAddEyeButton);
                mAddEyeButton.setImageBitmap(animBitmapAddEyeButton);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setEyeButtonImage();
            }
        });

        mHiglightButtonAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                mHighlightPaint.setARGB(255,0,value,0);
                if(mHighlightCanvas!=null) {
                    mHighlightCanvas.drawPath(mHighlightPath,mHighlightPaint);
                    mAddEyeButton.setImageBitmap(animBitmapAddEyeButton);
                }

                mAddEyeButton.invalidate();
            }
        });
    }

    public void addEyeButton(View view){
        if(ADDING_EYES){
            mEyeFrame.setOnTouchListener(myFocusTouchListner);
            ADDING_EYES=false;
            mHiglightButtonAnim.end();
        }else {
            mEyeFrame.setOnTouchListener(myEyePlacingTouch);
            ADDING_EYES=true;
            mHiglightButtonAnim.start();
        }

    }

    public void newSetButton(View view){

        mEyeGroup.addEyeSet(mEyeFrame);


    }

    public void setEyeButtonImage(){


        mAddEyeButton.setImageBitmap(EyeDrawables.drawEye(this, 50, 50, mEyedrawIndex));
    }


    public void drawableButton(View view){
        if(mEyedrawIndex <EyeDrawables.getNumDrawables()-1) mEyedrawIndex++;
        else mEyedrawIndex =0;
        ((Button) view).setText("draw:"+ mEyedrawIndex);

        if(mHiglightButtonAnim.isRunning()){
            mHiglightButtonAnim.cancel();
            mHiglightButtonAnim.start();
        }



        mEyePreview.setImageBitmap(EyeDrawables.drawEye(this, EYEBALL_WIDTH, EYEBALL_HEIGHT, mEyedrawIndex));

        setEyeButtonImage();



        mFadeOut.cancel();
        mEyePreview.setAlpha(1f);
        mFadeOut.start();

    }

    public void wiggleButton(View view){

        ObjectAnimator mWiggleUPDown= ObjectAnimator.ofFloat(mEyeFrame,"Y",600);
        ObjectAnimator mWiggleSide = ObjectAnimator.ofFloat(mEyeFrame,"X",0,400);
        mWiggleUPDown.setRepeatMode(ValueAnimator.REVERSE);
        mWiggleUPDown.setRepeatCount(3);
        mWiggleSide.setRepeatMode(ValueAnimator.REVERSE);
        mWiggleSide.setRepeatCount(3);

        AnimatorSet mWiggleAnim = new AnimatorSet();
        mWiggleAnim.play(mWiggleUPDown).after(mWiggleSide);
        mWiggleAnim.setDuration(300 * 3);
        mWiggleAnim.setInterpolator(new LinearInterpolator());
        mWiggleAnim.start();

    }

    public void watchButton(final View view){

        final int middleX =findViewById(R.id.container).getWidth()/2;
        final int middleY =findViewById(R.id.container).getHeight()/2;

        view.setX(middleX);
        view.setY(middleY);

        ObjectAnimator mWiggleUPDown= ObjectAnimator.ofFloat(view,"Y",middleY,0,mEyeFrameContainer.getHeight(),0);
        ObjectAnimator mWiggleSide = ObjectAnimator.ofFloat(view,"X",middleX,0,mEyeFrameContainer.getWidth(),0);
        mWiggleUPDown.setRepeatMode(ValueAnimator.REVERSE);
        mWiggleUPDown.setRepeatCount(3);
        mWiggleSide.setRepeatMode(ValueAnimator.REVERSE);
        mWiggleSide.setRepeatCount(3);

        AnimatorSet mWiggleAnim = new AnimatorSet();
        mWiggleAnim.play(mWiggleUPDown).after(mWiggleSide);
        //mWiggleAnim.setDuration(300*20);
        mWiggleAnim.setDuration(300*5);
        mWiggleAnim.setInterpolator(new LinearInterpolator());

        final EyeFocusBrainInterface.EyeBrainID myID = mEyeBrain.thinkRequest(EyeFocusBrainInterface.EyeBrainID.REQUEST_NEW_ID, EyeFocusBrainInterface.EyeBrainID.ID_FOR_NEW_ISSUE);

        mWiggleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(mBrainButton.isChecked()) mEyeBrain.thinkRequest(EyeBall.PLEASE_LOOSE_FOCUS, myID);
                else mEyeGroup.request(EyeBall.PLEASE_LOOSE_FOCUS);
                mEyeBrain.thinkRequest(EyeBall.PLEASE_LOOSE_FOCUS, myID);

                view.setX(0); view.setY(0);
            }
        });

        //if(mBrainButton.isChecked()) mEyeBrain.focusHere(view);
        //else mEyeGroup.focusHere(view);
        //todo pass this view call into a smart brain
        mEyeBrain.thinkFocusHere(view, myID);


        mWiggleAnim.start();
    }

    public void cameraButton(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                ((ImageView) findViewById(R.id.face)).setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    class eyePlace implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
                float x = event.getX();
                float y = event.getY();



                if(mEyeGroup ==null){
                    mEyeGroup = new EyeGroup();
                    //mEyeBrain = new testBrain(mEyeGroup);

                    findViewById(R.id.myButton).setEnabled(true);
                    findViewById(R.id.setButton).setEnabled(true);
                }



                //Log.d("TAG", "resource id" + getResources().getIdentifier("com.example.eyeballtest:drawable/" + myEyedraw.pupil_resource_name,null,null));
                //Log.d("TAG", "eyedraw id = "+ myEyedraw.pupil_resource_id);

                mEyeGroup.addEyeball(mEyeFrame,EYEBALL_HEIGHT, EYEBALL_WIDTH, x, y, EyeDrawables.getEyeDraw(mEyedrawIndex, getApplicationContext()));

            }

            return false;
        }




    }





    class fingerTouch implements View.OnTouchListener{


        private int mFinger1id;

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            int indexDown = event.getActionIndex();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:

                    mFinger1id = event.getPointerId(indexDown);

                    //for(int x=0;x<mEyeballs.size();x++) mEyeballs.get(x).thinkRequest((int) event.getX(index), (int) event.getY(index));

                    //sendtoEyeballsorEyeSet(event,indexDown);
                    mEyeGroup.focusHere((int) event.getX(), (int) event.getY());


                    //Log.d("touch","action  down");


                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    //Log.d("touch","action pointer down");

                    break;
                case MotionEvent.ACTION_POINTER_UP:


                    mEyeGroup.request(EyeBall.PLEASE_LOOSE_FOCUS);


                    break;
                case MotionEvent.ACTION_UP:

                    mEyeGroup.request(EyeBall.PLEASE_LOOSE_FOCUS);


                    break;


                case MotionEvent.ACTION_CANCEL:
                    //Log.d("touch","action cancel");
                    mEyeGroup.request(EyeBall.PLEASE_LOOSE_FOCUS);

                    break;

                case MotionEvent.ACTION_MOVE:
                    // Log.d("touch", "action move");

                    //int index = event.findPointerIndex(mFinger1id);


                    for(int x=0;x<event.getPointerCount();x++)
                    {
                        //if(event.getPointerId(x)==mFinger1id)  for(int i=0;i<mEyeballs.size();i++) mEyeballs.get(i).thinkRequest((int) event.getX(x), (int) event.getY(x));

                        if(event.getPointerId(x)==mFinger1id){

                            mEyeGroup.focusHere((int) event.getX(), (int) event.getY());

                        }

                    }



                    break;

                default:
                    break;
            }



            return false;
        }


    }

    class StupidBrainTouch implements View.OnTouchListener{


        private int mFinger1id;

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            int indexDown = event.getActionIndex();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:

                    mFinger1id = event.getPointerId(indexDown);



                    //sendtoEyeballsorEyeSet(event,indexDown);
                    mEyeBrain.focusHere((int) event.getX(), (int) event.getY());


                    //Log.d("touch","action  down");


                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    //Log.d("touch","action pointer down");

                    break;
                case MotionEvent.ACTION_POINTER_UP:


                    mEyeBrain.request(EyeBall.PLEASE_LOOSE_FOCUS);


                    break;
                case MotionEvent.ACTION_UP:

                    mEyeBrain.request(EyeBall.PLEASE_LOOSE_FOCUS);


                    break;


                case MotionEvent.ACTION_CANCEL:
                    //Log.d("touch","action cancel");
                    mEyeBrain.request(EyeBall.PLEASE_LOOSE_FOCUS);

                    break;

                case MotionEvent.ACTION_MOVE:
                    // Log.d("touch", "action move");

                    //int index = event.findPointerIndex(mFinger1id);


                    for(int x=0;x<event.getPointerCount();x++)
                    {

                        if(event.getPointerId(x)==mFinger1id){

                            mEyeBrain.focusHere((int) event.getX(), (int) event.getY());

                        }

                    }



                    break;

                default:
                    break;
            }



            return false;
        }


    }


}
