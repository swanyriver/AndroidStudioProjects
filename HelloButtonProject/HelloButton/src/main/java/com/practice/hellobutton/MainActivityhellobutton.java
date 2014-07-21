package com.practice.hellobutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

public class MainActivityhellobutton extends Activity implements View.OnClickListener {

    private Button myButton;
    private int distance=200;
    private ValueAnimator mLinearVanimTest;
    private int mNumberOfUpdates;
    private int mCurrentValue;
    private long mAnimDuration=600*8;
    private int mEndValue=100;
    private boolean mReachedEndValue=false;
    private ValueAnimator mInterpolatedVanimTest;
    private Integer mInterpolatedCurrentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hello_button);


        myButton = (Button) findViewById(R.id.myButton);
        myButton.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        distance=distance*-1;
        myButton.animate().translationX(distance).setDuration(mAnimDuration);

        this.startLinearValueanimator();
        this.startInterpolatedValueanimator();
    }

    private void startLinearValueanimator() {


        mLinearVanimTest = ValueAnimator.ofInt(0,mEndValue);
        mLinearVanimTest.setDuration(mAnimDuration);
        mLinearVanimTest.setInterpolator(new LinearInterpolator());
        mLinearVanimTest.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNumberOfUpdates++;
                if ((Integer) animation.getAnimatedValue() != mCurrentValue) {
                    mCurrentValue = (Integer) animation.getAnimatedValue();
                    ((TextView) findViewById(R.id.myLinearReadout)).setText(String.format("%d", mCurrentValue));
                }

                if ((Integer) animation.getAnimatedValue() == mEndValue) mReachedEndValue = true;

            }
        });
        mLinearVanimTest.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mNumberOfUpdates = 0;
                mCurrentValue = 0;
                mReachedEndValue = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                ((TextView) findViewById(R.id.myUpdateCountReadout)).setText(String.format("%d", mNumberOfUpdates));
                ((TextView) findViewById(R.id.myReachedEndReadout)).setText(""+mReachedEndValue);

            }

        });

        mLinearVanimTest.start();


    }

    private void startInterpolatedValueanimator() {


        mInterpolatedVanimTest = ValueAnimator.ofInt(0,mEndValue);
        mInterpolatedVanimTest.setDuration(mAnimDuration);
        mInterpolatedVanimTest.setInterpolator(new AccelerateDecelerateInterpolator());
        mInterpolatedVanimTest.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if ((Integer) animation.getAnimatedValue() != mInterpolatedCurrentValue) {
                    mInterpolatedCurrentValue = (Integer) animation.getAnimatedValue();
                    ((TextView) findViewById(R.id.myInterpolatedReadout)).setText(String.format("%d", mCurrentValue));
                }


            }
        });

        mInterpolatedCurrentValue = 0;
        mInterpolatedVanimTest.start();


    }
}
