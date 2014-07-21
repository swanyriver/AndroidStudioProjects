package com.example.eyeballtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class eyeMainActivity extends Activity {

    private FrameLayout mEyeContainer;
    //private eyeBall mEyeball;

    //private ArrayList<eyeBall> mEyeballs;

    //eyeset experiment
    private ToggleButton mJoinedToggle;
    private TextView mPrimaryText;
    private TextView mSecondFocusText;

    //cameraStuff
    private static int REQUEST_CODE=1;
    private Bitmap bitmap;
    private EyeSet mEyeSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_main_layout);


        mEyeContainer = (FrameLayout) findViewById(R.id.container);
        //mEyeballs = new ArrayList<eyeBall>();



        mEyeContainer.setClickable(true);
        //findViewById(R.id.container).setOnTouchListener(new fingerTouch());

        mEyeContainer.setOnTouchListener(new eyePlace());



        //eyeball set experimentation
        mJoinedToggle = (ToggleButton) findViewById(R.id.joinedToggle);
        mPrimaryText = (TextView) findViewById(R.id.primaryText);
        mPrimaryText.setTextColor(Color.RED);
        mPrimaryText.setTextSize(25);
        mSecondFocusText = (TextView) findViewById(R.id.secondfocus);
        mPrimaryText.setVisibility(View.INVISIBLE);
        mSecondFocusText.setVisibility(View.INVISIBLE);








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

                /*eyeBall newEye = new eyeBall(getApplicationContext(),100,150);
                newEye.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                newEye.setX(x - 75);
                newEye.setY(y-75);

                mEyeballs.add(newEye);*/

                if(mEyeSet==null){
                    mEyeSet = new EyeSet(getApplicationContext());
                    mEyeContainer.addView(mEyeSet);
                    findViewById(R.id.myButton).setEnabled(true);
                }

                mEyeSet.addEyeball(100,100,x,y);



            }

            return false;
        }
    }

    public void myButton(View view){
        mEyeContainer.setOnTouchListener(new fingerTouch());
        view.setVisibility(View.INVISIBLE);

    }

    class fingerTouch implements View.OnTouchListener{


        private int mFinger1id;

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            int indexDown = event.getActionIndex();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:



                    mFinger1id = event.getPointerId(indexDown);

                    //for(int x=0;x<mEyeballs.size();x++) mEyeballs.get(x).focusHere((int) event.getX(index), (int) event.getY(index));

                    //sendtoEyeballsorEyeSet(event,indexDown);
                    mEyeSet.focusHere((int) event.getX(),(int) event.getY());


                    //Log.d("touch","action  down");


                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    //Log.d("touch","action pointer down");

                    break;
                case MotionEvent.ACTION_POINTER_UP:

                    //if(event.getPointerId(indexDown)==mFinger1id)for(int x=0;x<mEyeballs.size();x++) mEyeballs.get(x).focusHere(eyeBall.PLEASE_LOOSE_FOCUS);
                    mEyeSet.focusHere(EyeSet.PLEASE_LOOSE_FOCUS);


                    break;
                case MotionEvent.ACTION_UP:

                    //for(int x=0;x<mEyeballs.size();x++) mEyeballs.get(x).focusHere(eyeBall.PLEASE_LOOSE_FOCUS);
                    mEyeSet.focusHere(EyeSet.PLEASE_LOOSE_FOCUS);


                    break;


                case MotionEvent.ACTION_CANCEL:
                    //Log.d("touch","action cancel");

                    break;

                case MotionEvent.ACTION_MOVE:
                    // Log.d("touch", "action move");

                    //int index = event.findPointerIndex(mFinger1id);


                    for(int x=0;x<event.getPointerCount();x++)
                    {
                        //if(event.getPointerId(x)==mFinger1id)  for(int i=0;i<mEyeballs.size();i++) mEyeballs.get(i).focusHere((int) event.getX(x), (int) event.getY(x));

                        if(event.getPointerId(x)==mFinger1id){

                            mEyeSet.focusHere((int) event.getX(),(int) event.getY());

                        }

                    }



                    break;

                default:
                    break;
            }



            return false;
        }

        /*private void sendtoEyeballsorEyeSet(MotionEvent event, int index) {

            int fingerX = (int) event.getX(index);
            int fingerY = (int) event.getY(index);

            if(!mJoinedToggle.isChecked()) for(int x=0;x<mEyeballs.size();x++) mEyeballs.get(x).focusHere(fingerX, fingerY);
            else{

                //get distance, pick primary, send focus to one, send offset to other

                float distance = mEyeContainer.getHeight()*3;
                int primary = 1;

                for(int x=0;x<mEyeballs.size();x++) {
                    if(mEyeballs.get(x).getDistancetoTouch(fingerX,fingerY)<distance){
                        distance=mEyeballs.get(x).getDistancetoTouch(fingerX,fingerY);
                        primary=x;
                    }

                }

                mEyeballs.get(primary).focusHere(fingerX, fingerY);



                for(int x=0;x<mEyeballs.size();x++) if(x!=primary){

                    int xOffset = 0; int yOffset=0;
                    if(!((fingerX>mEyeballs.get(primary).mEyeballCenterWindowX&&fingerX<mEyeballs.get(x).mEyeballCenterWindowX)||(fingerX<mEyeballs.get(primary).mEyeballCenterWindowX&&fingerX>mEyeballs.get(x).mEyeballCenterWindowX)))
                        xOffset=mEyeballs.get(x).mEyeballCenterWindowX-mEyeballs.get(primary).mEyeballCenterWindowX;
                    if((!(fingerY>mEyeballs.get(primary).mEyeballCenterWindowY&&fingerY<mEyeballs.get(x).mEyeballCenterWindowY)||(fingerY<mEyeballs.get(primary).mEyeballCenterWindowY&&fingerY>mEyeballs.get(x).mEyeballCenterWindowY)))
                        yOffset=mEyeballs.get(x).mEyeballCenterWindowY-mEyeballs.get(primary).mEyeballCenterWindowY;

                    mEyeballs.get(x).focusHere(
                        fingerX + xOffset,
                        fingerY + yOffset);


                    Log.d("focus", "linked, primary:" + primary + " xdif=" + (mEyeballs.get(primary).mEyeballCenterWindowX - mEyeballs.get(x).mEyeballCenterWindowX) +
                            " ydif = " + (mEyeballs.get(primary).mEyeballCenterWindowY - mEyeballs.get(x).mEyeballCenterWindowY) );

                    mPrimaryText.setX(mEyeballs.get(primary).mEyeballCenterWindowX-100);
                    mPrimaryText.setY(mEyeballs.get(primary).mEyeballCenterWindowY+125);

                    mSecondFocusText.setX(fingerX + xOffset);
                    mSecondFocusText.setY(fingerY + yOffset);

                }
            }
        }*/
    }




}
