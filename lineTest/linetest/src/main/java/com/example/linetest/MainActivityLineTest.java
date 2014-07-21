package com.example.linetest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivityLineTest extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_line_test);

        lineDraw myLinedraw = new lineDraw(this);
        myLinedraw.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.container)).addView(myLinedraw);




    }




}

class lineDraw extends ImageView{



    ///for visualization
    Path mLinePath = new Path();
    Paint mLinePaint = new Paint();

    int mBeginingX;
    int mBeginingY;
    private ArrayList<Line> mLines = new ArrayList<Line>();


    lineDraw(Context context) {
        super(context);
        setClickable(true);

        ///for visualization
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(10);


    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        canvas.drawPath(mLinePath,mLinePaint);



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
            mBeginingX= (int) event.getX();
            mBeginingY= (int) event.getY();

        }else if(event.getActionMasked()==MotionEvent.ACTION_UP){
            mLines.add(new Line(mBeginingX,mBeginingY,(int) event.getX(),(int)event.getY()));

            int current = mLines.size()-1;
            mLinePath.moveTo(mLines.get(current).start.x,mLines.get(current).start.y);
            mLinePath.lineTo(mLines.get(current).end.x, mLines.get(current).end.y);

            Line perpLine=makePerpindicularCenterIntersectedLine(mLines.get(current));
            mLinePath.moveTo(perpLine.start.x,perpLine.start.y);
            mLinePath.lineTo(perpLine.end.x,perpLine.end.y);

            for(int x=0;x<mLines.size()-1;x++){
                Intersection intersect = get_line_intersection(mLines.get(x),mLines.get(current));
                if(intersect.intersects){
                    Log.d("intersect", "these lines intersect");
                    mLinePath.addCircle(intersect.intersectionPoint.x,intersect.intersectionPoint.y,15, Path.Direction.CW);
                }
            }

            invalidate();

        }

        return super.onTouchEvent(event);
    }



    ////////////////////line Work

    class Line{

        public Point start;
        public Point end;

        Line(int xStart,int yStart, int xEnd, int yEnd) {
            start = new Point(xStart,yStart);
            end = new Point(xEnd,yEnd);
        }

    }

    class Intersection{
        boolean intersects = false;
        Point intersectionPoint = new Point();
    }

    public Line makePerpindicularCenterIntersectedLine(Line originalLine){


        float scaleFactor = .2f;

        int dx = originalLine.end.x - originalLine.start.x;
        int dy = originalLine.end.y - originalLine.start.y;

        int orginalLineMidX = originalLine.start.x + Math.round(.5f * dx);
        int orginalLineMidY = originalLine.start.y + Math.round(.5f * dy);


        int perpdx = dy;
        int perpdy = dx;

        int perpLineStartX = (int) (orginalLineMidX + ((scaleFactor * perpdx)*-1));
        int perpLineStartY = (int) (orginalLineMidY + (scaleFactor * perpdy));
        int perpLineEndX = (int) (orginalLineMidX + (scaleFactor * perpdx));
        int perpLineEndY = (int) (orginalLineMidY + ((scaleFactor * perpdy)*-1));

        //return new Point(orginalLineMidX,orginalLineMidY);
        return new Line(perpLineStartX,perpLineStartY,perpLineEndX,perpLineEndY);
    }


    Intersection get_line_intersection(Line lineOne, Line lineTwo){

        Intersection thisIntersection = new Intersection();


        float s1_x, s1_y, s2_x, s2_y;
        s1_x = lineOne.end.x - lineOne.start.x;     s1_y = lineOne.end.y - lineOne.start.y;
        s2_x = lineTwo.end.x - lineTwo.start.x;     s2_y = lineTwo.end.y - lineTwo.start.y;

        float s, t;
        s = (-s1_y * (lineOne.start.x - lineTwo.start.x) + s1_x * (lineOne.start.y - lineTwo.start.y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (lineOne.start.y - lineTwo.start.y) - s2_y * (lineOne.start.x - lineTwo.start.x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            // Collision detected
            thisIntersection.intersects=true;
            thisIntersection.intersectionPoint.x = (int) (lineOne.start.x + (t * s1_x));
            thisIntersection.intersectionPoint.y = (int) (lineOne.start.y +  (t * s1_y));

        }

        return thisIntersection;
    }

}
