package EyeBalls;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;
import android.widget.FrameLayout;


import java.util.ArrayList;

public class EyeSet implements EyeFocus{



    public boolean POSITION_IS_FIXED = true;


    private ArrayList<EyeBallINT> mEyeballs;
    private int mNumEyeballs=0;
    private ArrayList<ArrayList<Line>> mBorderLines = new ArrayList<ArrayList<Line>>();
    private FrameLayout mEyeFrame;

    private ValueAnimator mFollowViewAnim;




    /**
     * to be used for constructing single empty, eyeset
     * @param eyeFrame  Frame that the Eyeset will be drawn in,  adds image views to this frame
     */
    public EyeSet(FrameLayout eyeFrame) {

        setEyeFrame(eyeFrame);
        initEyeSet();

    }
    public void setEyeFrame(FrameLayout eyeFrame){
        mEyeFrame=eyeFrame;
    }


    private void initEyeSet() {


        mEyeballs = new ArrayList<EyeBallINT>();

        mFollowViewAnim = ValueAnimator.ofInt(0,1);
        mFollowViewAnim.setDuration(1200);
        mFollowViewAnim.setRepeatCount(ValueAnimator.INFINITE);


    }


    public void addEyeball(int height,int width,float localx,float localy, EyeDraw eyeDraw){
        final EyeBall newEye = new EyeBall(mEyeFrame.getContext(),width,height,eyeDraw);
        //newEye.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newEye.setX(localx - height/2);
        newEye.setY(localy-height/2);


        mEyeballs.add(newEye);

        mEyeFrame.addView(newEye);
        //mNumEyeballs++;


        newEye.post(new Runnable() {
            @Override
            public void run() {

                newEye.findCenterInWindow();
                //Log.d("Center", "posted, access membervariables  X:" + newEye.mEyeballCenterWindowX + " Y:" + newEye.mEyeballCenterWindowY);

                creatBorderlines(newEye);
                mNumEyeballs++;
            }
        });



    }



    private void creatBorderlines(EyeBall eye){

        int indexofnewEye = mEyeballs.indexOf(eye);

        mBorderLines.add(indexofnewEye, new ArrayList<Line>());
        for(int i=0;i<mNumEyeballs;i++)mBorderLines.get(i).add(null);
        for(int x=0;x<mNumEyeballs;x++){

            if(mEyeballs.get(x)!=eye){



                Line borderLine = getPerpindicularCenterIntersectedLine(new Line(eye.getmEyeballCenterWindowX(),eye.getmEyeballCenterWindowY(),
                        mEyeballs.get(x).getmEyeballCenterWindowX(),mEyeballs.get(x).getmEyeballCenterWindowY()));


                mBorderLines.get(x).add(indexofnewEye,borderLine);
                mBorderLines.get(indexofnewEye).add(x,borderLine);



            }
        }

    }


    public void request(int RequestCode){
        if(RequestCode== EyeBall.PLEASE_LOOSE_FOCUS){
            mFollowViewAnim.cancel();
            for(int x=0;x<mNumEyeballs;x++) mEyeballs.get(x).focusHere(EyeBall.PLEASE_LOOSE_FOCUS);
        }
    }

    public void focusHere(final View view){


        mFollowViewAnim.removeAllUpdateListeners();

        mFollowViewAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int[] locationinWindow = new int[2];
                view.getLocationInWindow(locationinWindow);
                focusHere(locationinWindow[0]+view.getWidth()/2,
                        locationinWindow[1]+view.getHeight()/2);
                //todo add translation
            }
        });


        mFollowViewAnim.start();

    }

    public void focusHere(int focusRequestX, int focusRequestY){

        if(POSITION_IS_FIXED && mNumEyeballs>1){

            int primary = getIndexofClosest(focusRequestX, focusRequestY);
            mEyeballs.get(primary).focusHere(focusRequestX, focusRequestY);



            for(int x=0;x<mNumEyeballs;x++) if(x!=primary){

                int xOffset = focusRequestX+ (mEyeballs.get(x).getmEyeballCenterWindowX()-mEyeballs.get(primary).getmEyeballCenterWindowX());
                int yOffset= focusRequestY+ (mEyeballs.get(x).getmEyeballCenterWindowY()-mEyeballs.get(primary).getmEyeballCenterWindowY());;



                //check if it intersects lineA, perpindicular and center of line between eyex and primary
                Intersection intersect = get_line_intersection(mBorderLines.get(primary).get(x),new Line(focusRequestX,focusRequestY,xOffset,yOffset));
                if(intersect.intersects){ 
                    xOffset=intersect.intersectionPoint.x;
                    yOffset=intersect.intersectionPoint.y;


                }
                mEyeballs.get(x).focusHere(xOffset,yOffset);

            }
        }else for(int i=0;i<mNumEyeballs;i++) mEyeballs.get(i).focusHere(focusRequestX,focusRequestY);

    }

    /**
     * used for stupid brains to bypass EYESET logic, use with eyegroup
     * @param focusRequestX
     * @param focusRequestY
     * @param index eyeball to send thinkRequest to
     */
    @Override
    public void focusHere(int focusRequestX, int focusRequestY, int index) {
        if(index<mNumEyeballs) mEyeballs.get(index).focusHere(focusRequestX,focusRequestY);
    }

    ////////////////////////////////
    ////////////PUBLIC GETTERS//////
    ///////////////////////////////


    @Override
    public Point getCenterinWindow(int index) {
       if(index<mNumEyeballs) return new Point(mEyeballs.get(index).getmEyeballCenterWindowX(),mEyeballs.get(index).getmEyeballCenterWindowY());
       else return null;
    }


    public int getIndexofClosest(int x, int y) {
        int primary = 0;
        float shortestDistance = 0;
        for(int i=0;i<mNumEyeballs;i++) {
            float distanceToTouch = EyeBall.getDistancetoNonSQRTD(x, y, mEyeballs.get(i).getmEyeballCenterWindowX(), mEyeballs.get(i).getmEyeballCenterWindowY());
            if(distanceToTouch<shortestDistance||i==0){
                shortestDistance=distanceToTouch;
                primary=i;
            }
        }
        return primary;
    }

    static int getIndexofClosest(int x, int y, Point[] points) {
        int primary = 0;
        float shortestDistance = 0;
        for(int i=0;i<points.length;i++) {
            //float distanceToTouch = Eyeball.getDistancetoNonSQRTD(x, y, mEyeballs.get(i).getmEyeballCenterWindowX(), mEyeballs.get(i).getmEyeballCenterWindowY());
            float distanceToTouch = EyeBall.getDistancetoNonSQRTD(x, y, points[i].x, points[i].y);
            if(distanceToTouch<shortestDistance||i==0){
                shortestDistance=distanceToTouch;
                primary=i;
            }
        }
        return primary;
    }

    @Override
    public int getmNumEyesets() {
        return mNumEyeballs;
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

    private Line getPerpindicularCenterIntersectedLine(Line originalLine){


        int scaleFactor = 4;

        int dx = originalLine.end.x - originalLine.start.x;
        int dy = originalLine.end.y - originalLine.start.y;

        int orginalLineMidX = originalLine.start.x + Math.round(.5f * dx);
        int orginalLineMidY = originalLine.start.y + Math.round(.5f * dy);


        int perpdx = dy;
        int perpdy = dx;

        int perpLineStartX = orginalLineMidX + ((scaleFactor * perpdx)*-1);
        int perpLineStartY = orginalLineMidY + (scaleFactor * perpdy);
        int perpLineEndX = orginalLineMidX + (scaleFactor * perpdx);
        int perpLineEndY = orginalLineMidY + ((scaleFactor * perpdy)*-1);

        //return new Point(orginalLineMidX,orginalLineMidY);
        return new Line(perpLineStartX,perpLineStartY,perpLineEndX,perpLineEndY);
    }

    // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines
    // intersect the intersection point may be stored in the floats i_x and i_y.
    private Intersection get_line_intersection(Line lineOne, Line lineTwo){

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
