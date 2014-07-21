package com.example.TextnCircleSelect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivityTextnCircle extends Activity {

    private ImageView mDestinationText;
    private TextView mSourceText;
    private Paint mTextPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_text_circle);

        mSourceText = (TextView) findViewById(R.id.text1);
        mDestinationText = (ImageView) findViewById(R.id.text2);

        findViewById(R.id.container).post(new Runnable() {
            @Override
            public void run() {
                mTextPaint = mSourceText.getPaint();

               // mDestinationText.setWidth(mSourceText.getWidth());
               // mDestinationText.setHeight(mSourceText.getHeight());


                mDestinationText.setX(mSourceText.getX());
                mDestinationText.setY(mSourceText.getY());

                mBitmap = Bitmap.createBitmap(mSourceText.getWidth(),mSourceText.getHeight(), Bitmap.Config.ARGB_8888);

                mCanvas = new Canvas(mBitmap);
            }
        });

    }

    public void Button (View view){

        mCanvas.drawColor(Color.RED);
        mCanvas.drawText("Howdy",0,0,mTextPaint);

        mDestinationText.setImageBitmap(mBitmap);
        mSourceText.setVisibility(View.INVISIBLE);

    }

}
