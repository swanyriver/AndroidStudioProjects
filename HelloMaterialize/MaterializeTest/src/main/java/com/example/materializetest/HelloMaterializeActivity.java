package com.example.materializetest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class HelloMaterializeActivity extends Activity implements View.OnClickListener{

    private Bitmap mAnimationBM;
    private Bitmap mDorisHeadBMSource;
    private ImageView mMyImage;
    private int mHeightMyImage;
    private int mWidthtMyImage;
    private int mNumberofDivisions=20;
    private int[] mDivisionPointsWidth;
    private int[] mDivisionPointsHeight;
    private Rect[][] mRectangles;
    private ArrayList<Rect> mRectangleList;
    private ValueAnimator mAnimMaterializeOne;
    private int mCellsRevealed=0;
    //private int mNumberofCells = (int) Math.pow(mNumberofDivisions,2);
    private long mDuration=900;
    private Float mGridPixelWidth =3f;
    private ImageView mGridImageView;


    private ObjectAnimator mTranslationAnim;
    private ObjectAnimator mAlphaAnim;
    private AnimatorSet mAnimSet;
    private ArrayList<Animator> mAnimatorList;

    private ToggleButton mAlphaToggle;
    private ToggleButton mDrawToggle;
    private SeekBar mDurationSeekBar;
    private SeekBar mDivisionSeekBar;

    private int mDivisionMax = 50;
    private int mDivisionMin = 3;
    private long mDurationInterval = 150;
    private int mDurationIntervalSteps = 20;



    private int mAnimationSelected =2;


    private boolean mDrawing = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_hello_materialize);

        this.findViewById(R.id.myFirstTryButton).setOnClickListener(this);
        mMyImage=(ImageView) findViewById(R.id.myImage);
        mMyImage.setClickable(true);

        mAlphaToggle = (ToggleButton) findViewById(R.id.AlphaToggleButton);
        mDrawToggle = (ToggleButton) findViewById(R.id.drawToggleButton);


        mTranslationAnim = ObjectAnimator.ofFloat(mMyImage,"TranslationY",-1,0);
        mAlphaAnim = ObjectAnimator.ofFloat(mMyImage, "Alpha",0f,1f);
        mAnimMaterializeOne = ValueAnimator.ofInt();

        mAnimatorList=new ArrayList<Animator>();
        mAnimatorList.add(mAnimMaterializeOne);


        //add other animations programatically here, if checked
        if(mAlphaToggle.isChecked()) mAnimatorList.add(mAlphaAnim);
        //if(mTranslateToggle.isChecked()) mAnimatorList.add(mTranslationAnim);


        mAnimSet = new AnimatorSet();
        mAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimSet.setDuration(mDuration);

        mDivisionSeekBar = (SeekBar) findViewById(R.id.divisionSeek);
        mDurationSeekBar = (SeekBar) findViewById(R.id.durationSeek);

        mDivisionSeekBar.setMax(mDivisionMax);
        mDivisionSeekBar.setProgress(mNumberofDivisions);
        mDurationSeekBar.setMax(mDurationIntervalSteps);
        mDurationSeekBar.setProgress((int) (mDuration/mDurationInterval-1));

        setDivisionSeekBarText();
        setDurationSeekBarText();

        mDivisionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if(user){
                    if(progress>=3)mNumberofDivisions=progress;
                    else mNumberofDivisions=3;
                    setDivisionSeekBarText();

                    createRectangles();
                    drawRectangles();


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(!((ToggleButton) findViewById(R.id.gridToggleButton)).isChecked()) showRectangles();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!((ToggleButton) findViewById(R.id.gridToggleButton)).isChecked()) hideRectangles();
                createMaterializeOneAnimation();

            }
        });
        mDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if(user) mDuration=progress*mDurationInterval+mDurationInterval;
                setDurationSeekBarText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        mMyImage.post(new Runnable() {
            @Override
            public void run() {


                mHeightMyImage=mMyImage.getHeight();
                mWidthtMyImage=mMyImage.getWidth();

                BitmapFactory.Options myOptions = new BitmapFactory.Options();
                myOptions.inMutable = true;
                //myOptions.outHeight=mHeightMyImage;
                //myOptions.outWidth=mWidthtMyImage;

                //create 2 Bitmaps, exact size as image view
                mAnimationBM = Bitmap.createBitmap(mWidthtMyImage,mHeightMyImage, Bitmap.Config.ARGB_8888);
                Canvas mAnimationCanvas = new Canvas(mAnimationBM);

                mDorisHeadBMSource = BitmapFactory.decodeResource(getResources(),R.drawable.dorisheadsmall, myOptions);
                mDorisHeadBMSource = Bitmap.createScaledBitmap(mDorisHeadBMSource,mWidthtMyImage,mHeightMyImage,false);


                //clear animationBM
                mAnimationBM.eraseColor(Color.TRANSPARENT);
                mMyImage.setImageBitmap(mAnimationBM);



                //create rectangles
                createRectangles();


                mGridImageView = new ImageView(HelloMaterializeActivity.this);
                mGridImageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                Bitmap gridBM = Bitmap.createBitmap(mWidthtMyImage,mHeightMyImage, Bitmap.Config.ARGB_8888);
                gridBM.eraseColor(Color.TRANSPARENT);
                mGridImageView.setImageBitmap(gridBM);
                ((FrameLayout) findViewById(R.id.myFrame)).addView(mGridImageView);
                drawRectangles();

                boolean gridOn=((ToggleButton)findViewById(R.id.gridToggleButton)).isChecked();
                if(gridOn)showRectangles();
                else hideRectangles();


                //create animation
                createMaterializeOneAnimation();

            }
        });





    }

    private void setDivisionSeekBarText() {
        ((TextView)findViewById(R.id.myDivisionReadout)).setText(""+mNumberofDivisions);

    }
    private void setDurationSeekBarText() {
        ((TextView)findViewById(R.id.myDurationReadout)).setText(""+(int) mDuration);

    }


    private void createRectangles() {
        mDivisionPointsWidth = new int[mNumberofDivisions+1];
        mDivisionPointsHeight = new int[mNumberofDivisions+1];
        mRectangles = new Rect[mNumberofDivisions][mNumberofDivisions];

        int widthdivisor=mWidthtMyImage/mNumberofDivisions;
        int heightdivisor=mHeightMyImage/mNumberofDivisions;


        for (int x=0;x<mNumberofDivisions;x++){
            mDivisionPointsWidth[x]=x*widthdivisor;
            mDivisionPointsHeight[x]=x*heightdivisor;
        }

        mDivisionPointsWidth[mNumberofDivisions]=mWidthtMyImage;
        mDivisionPointsHeight[mNumberofDivisions]=mHeightMyImage;


        for (int x=0;x<mNumberofDivisions;x++){
            for (int y=0;y<mNumberofDivisions;y++){
                mRectangles[x][y]=new Rect(mDivisionPointsWidth[x],mDivisionPointsHeight[y],
                        mDivisionPointsWidth[x+1],mDivisionPointsHeight[y+1]);
            }
        }
    }

    private void drawRectangles() {


        Bitmap gridBM = Bitmap.createBitmap(mWidthtMyImage,mHeightMyImage, Bitmap.Config.ARGB_8888);
        gridBM.eraseColor(Color.TRANSPARENT);

        Canvas gridCanvas = new Canvas(gridBM);
        Paint myPaint =  new Paint();
        myPaint.setColor(Color.RED);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(mGridPixelWidth);
        Path myPath = new Path();


        for (int x=0;x<=mNumberofDivisions;x++){

            myPath.moveTo(mDivisionPointsWidth[x],0);
            myPath.lineTo(mDivisionPointsWidth[x],mHeightMyImage);

        }
        for (int y=0;y<=mNumberofDivisions;y++){

            myPath.moveTo(0, mDivisionPointsHeight[y]);
            myPath.lineTo(mWidthtMyImage, mDivisionPointsHeight[y]);
        }
        gridCanvas.drawPath(myPath,myPaint);


        mGridImageView.setImageBitmap(gridBM);


    }

    private void showRectangles(){
        mGridImageView.setVisibility(View.VISIBLE);
    }

    private void hideRectangles() {
        mGridImageView.setVisibility(View.INVISIBLE);
    }


    private void createMaterializeOneAnimation() {



        mAnimMaterializeOne.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {


                findViewById(R.id.myFirstTryButton).setEnabled(false);

                mRectangleList = new ArrayList<Rect>();
                for (int x=0;x<mNumberofDivisions;x++){
                    for (int y=0;y<mNumberofDivisions;y++){
                        mRectangleList.add(mRectangles[x][y]);
                    }
                }

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                findViewById(R.id.myFirstTryButton).setEnabled(true);
                findViewById(R.id.divisionSeek).setEnabled(false);

                //mMyImage.setImageBitmap(mDorisHeadBMSource);
                if(mCellsRevealed>0) {
                    ((Button) findViewById(R.id.myFirstTryButton)).setText("De-Materialize Doris");
                    mDrawing =false;
                }
                else{
                    ((Button) findViewById(R.id.myFirstTryButton)).setText("Materialize Doris");
                    mDrawing=true;
                }

            }
        });

        mAnimMaterializeOne.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (Integer) valueAnimator.getAnimatedValue();

                if(mAnimationSelected==1){


                    if (animatedValue>mCellsRevealed){
                        int cellsToReveal = animatedValue-mCellsRevealed;
                        mCellsRevealed=animatedValue;
                        for (int x=0;x<cellsToReveal;x++){
                            int rectIndex = new Random().nextInt(mRectangleList.size());
                            makeRectVisible(mRectangleList.get(rectIndex));
                            mRectangleList.remove(rectIndex);
                        }

                    }else if (animatedValue<mCellsRevealed){
                        int cellsToHide = mCellsRevealed-animatedValue;
                        mCellsRevealed=animatedValue;
                        for (int x=0;x<cellsToHide;x++){
                            int rectIndex = new Random().nextInt(mRectangleList.size());
                            makeRectHidden(mRectangleList.get(rectIndex));
                            mRectangleList.remove(rectIndex);
                        }

                    }
                }

                else if(mAnimationSelected==2){


                    if (animatedValue>mCellsRevealed){
                        int cellsToReveal = animatedValue-mCellsRevealed;
                        mCellsRevealed=animatedValue;
                        for (int x=0;x<cellsToReveal;x++){
                            int rectIndex = new Random().nextInt(mRectangleList.size());
                            //makeRectVisible(mRectangleList.get(rectIndex));
                            new rectScaller(mRectangleList.get(rectIndex),true);
                            mRectangleList.remove(rectIndex);
                        }

                    }else if (animatedValue<mCellsRevealed){
                        int cellsToHide = mCellsRevealed-animatedValue;
                        mCellsRevealed=animatedValue;
                        for (int x=0;x<cellsToHide;x++){
                            int rectIndex = new Random().nextInt(mRectangleList.size());
                            //makeRectHidden(mRectangleList.get(rectIndex));
                            new rectScaller(mRectangleList.get(rectIndex),false);
                            mRectangleList.remove(rectIndex);
                        }

                    }
                }




            }
        });


        mAnimMaterializeOne.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimMaterializeOne.setDuration(mDuration);



    }

    class rectScaller{

        int OVERLAP_FACTOR = 5;

        boolean mAppear;
        Rect sourceRect;
        Rect scalleRect;

        ValueAnimator scaller;

        int halfwidth;
        int halfheight;
        int middlewidth;
        int middleheight;

        rectScaller(Rect rect, boolean appear) {
            sourceRect=rect;

            mAppear=appear;

            halfwidth = sourceRect.width()/2;
            halfheight = sourceRect.height()/2;
            middleheight=sourceRect.centerY();
            middlewidth=sourceRect.centerX();

            makeAppearAniation();


            scaller.setDuration((mDuration/(mNumberofDivisions*mNumberofDivisions))*OVERLAP_FACTOR);
            scaller.start();

        }



        private void makeAppearAniation() {
            scaller = ValueAnimator.ofFloat(1,0);
            scaller.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    float animatedfraction = valueAnimator.getAnimatedFraction();
                    scalleRect= new Rect();
                    //scalleRect.offset((int)(200*animatedfraction),(int)(200*animatedfraction));
                    scalleRect.top = middleheight-(int)(halfheight*animatedfraction);
                    scalleRect.bottom = middleheight+(int)(halfheight*animatedfraction);
                    scalleRect.left = middlewidth-(int)(halfwidth*animatedfraction);
                    scalleRect.right = middlewidth+(int)(halfwidth*animatedfraction);



                    if(mAppear) makeRectVisible(scalleRect);
                    else makeRectHidden(scalleRect);
                }

            });

            scaller.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    if(mAppear) makeRectVisible(sourceRect);
                    else makeRectHidden(sourceRect);

                    super.onAnimationEnd(animation);
                }
            });

        }


    }

    private void makeRectHidden(Rect hideRect) {
        Canvas AnimationCanvas = new Canvas(mAnimationBM);
        AnimationCanvas.save();
        AnimationCanvas.clipRect(hideRect);
        AnimationCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        AnimationCanvas.restore();

       //mMyImage.setImageBitmap(mAnimationBM);

        Rect invaladeRect = new Rect(hideRect.left,hideRect.top,hideRect.right,hideRect.bottom);
        mMyImage.invalidate(invaladeRect);
    }

    private void makeRectVisible(Rect revealRect) {
        Canvas AnimationCanvas = new Canvas(mAnimationBM);
        AnimationCanvas.drawBitmap(mDorisHeadBMSource,revealRect,revealRect,new Paint());
       // mMyImage.setImageBitmap(mAnimationBM);
        Rect invaladeRect = new Rect(revealRect.left,revealRect.top,revealRect.right,revealRect.bottom);
        mMyImage.invalidate(invaladeRect);
    }

    @Override
    public void onClick(View view) {

        if (mDivisionSeekBar.getProgress()!=mNumberofDivisions){

        }

        setAnimationDirection();

        mAnimSet = new AnimatorSet();
        mAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimSet.setDuration(mDuration);
        mAnimSet.playTogether(mAnimatorList);



        mAnimSet.start();

        //mAnimMaterializeOne.start();

    }



    private void setAnimationDirection() {
        if(mCellsRevealed>0) {
            //dematerialize
            mCellsRevealed=mNumberofDivisions*mNumberofDivisions;
            mAnimMaterializeOne.setIntValues(mNumberofDivisions*mNumberofDivisions,0);
            mTranslationAnim.setFloatValues(0,mHeightMyImage*-1);
            mAlphaAnim.setFloatValues(1f,0f);
        }
        else{
            //materialize

            mAnimMaterializeOne.setIntValues(0,mNumberofDivisions*mNumberofDivisions);
            mTranslationAnim.setFloatValues(mHeightMyImage*-1,0);
            mAlphaAnim.setFloatValues(0f,1f);

        }
    }


    public void GridToggleClicked(View gridToggle){
        Boolean gridOn=((ToggleButton) gridToggle).isChecked();

        if(gridOn) showRectangles();
        else hideRectangles();

    }
    public void AlphaToggleClicked(View alphaToggle){
        Boolean on=((ToggleButton) alphaToggle).isChecked();

        if(on && !mAnimatorList.contains(mTranslationAnim)) mAnimatorList.add(mAlphaAnim);
        else {
            if(mAnimatorList.contains(mAlphaAnim)) mAnimatorList.remove(mAlphaAnim);
        }

    }public void drawToggleClicked(View TransToggle){
        Boolean on=((ToggleButton) TransToggle).isChecked();

        if(on){

            findViewById(R.id.myFirstTryButton).setEnabled(false);
            findViewById(R.id.AlphaToggleButton).setEnabled(false);
            findViewById(R.id.durationSeek).setEnabled(false);
            findViewById(R.id.divisionSeek).setEnabled(false);

            mMyImage.setOnTouchListener(new drawTouch());


        }
        else {

            mMyImage.setOnTouchListener(null);

            findViewById(R.id.myFirstTryButton).setEnabled(true);
            findViewById(R.id.AlphaToggleButton).setEnabled(true);
            findViewById(R.id.durationSeek).setEnabled(true);
            //findViewById(R.id.divisionSeek).setEnabled(true);

            Canvas AnimationCanvas = new Canvas(mAnimationBM);

            if(mDrawing){
                //erase


                AnimationCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);


            }else{
                //redraw

                AnimationCanvas.drawBitmap(mDorisHeadBMSource,0,0,new Paint());

            }
            mMyImage.invalidate();

        }

    }




    class drawTouch implements View.OnTouchListener{


        private Boolean[][] mRectDrawn;


        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:

                    if(mRectDrawn==null){
                        mRectDrawn = new Boolean[mNumberofDivisions][mNumberofDivisions];

                        for(int i=0;i<mNumberofDivisions;i++)Arrays.fill(mRectDrawn[i],false);

                    }

                    checkRectangle(event.getX(),event.getY());

                    //Log.d("touch","action  down");

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    //Log.d("touch","action pointer down");


                    checkRectangle(event.getX(),event.getY());


                    break;
                case MotionEvent.ACTION_POINTER_UP:


                    break;
                case MotionEvent.ACTION_UP:

                    break;

                case MotionEvent.ACTION_CANCEL:
                    //Log.d("touch","action cancel");

                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d("touch", "action move");

                    for (int i = 0; i < event.getPointerCount(); i++) {

                        checkRectangle(event.getX(i),event.getY(i));
                    }



                    break;

                default:
                    break;
            }



            return false;
        }


        private void checkRectangle(float x, float y){



            int collumn =(int) x/mDivisionPointsWidth[1];
            int row =(int) y/mDivisionPointsHeight[1];

            if(collumn<0||collumn>=mNumberofDivisions||row<0||row>=mNumberofDivisions) return;

            if(!mRectDrawn[collumn][row]){
                mRectDrawn[collumn][row]=true;

                if(mDrawing){
                    makeRectVisible(mRectangles[collumn][row]);

                    //mCellsRevealed++;

                    //if (mCellsRevealed==mNumberofDivisions*mNumberofDivisions){
                        //done drawing
                    //}

                }else {
                    makeRectHidden(mRectangles[collumn][row]);
                    //mCellsRevealed--;

                    //mRectangleList.remove(mRectangles[collumn][row]);

                    //
                    //if(mCellsRevealed==0){
                        //done erasing
                    //}
                }

            }
        }

    }




}


