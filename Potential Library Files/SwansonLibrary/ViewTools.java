package SwansonLibrary;

import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;


/**
 * Created by Brandon on 4/24/14.
 */
public class ViewTools {

    public static double FULLCIRCLE = Math.PI*2;

    public static final class RadianAngles{

        public static double RIGHT=0;
        public static double TOP=1.5*Math.PI;
        public static double LEFT=Math.PI;
        public static double BOTTOM=Math.PI*.5;

    }


    static public void findCenterInWindow(View view, int location[]) {
        view.getLocationInWindow(location);
        location[0]+=view.getWidth()/2;
        location[1]+=view.getHeight()/2;

        //Log.d("Center", "find CenterinWindow  X:" + mEyeballCenterWindowX + " Y:" + mEyeballCenterWindowY);

    }


    static public double lengthRatioDistance(Line line, double length){return lengthRatioDistance(line.start,line.end,length);}
    static public double lengthRatioDistance(Point start, Point end, double length){
        return lengthRatioDistance(start.x,start.y,end.x,end.y,length);
    }
    static public double lengthRatioDistance(int x, int y, int x2, int y2, double length){
        return length/Math.sqrt(getDistancetoNonSQRTD(x,y,x2,y2));
    }

    static public boolean lengthLessThanDistance(Line line, double length){return lengthLessThanDistance(line.start, line.end, length);}
    static public boolean lengthLessThanDistance(Point start, Point end, double length){
        return lengthLessThanDistance(start.x, start.y, end.x, end.y, length);
    }
    static public boolean lengthLessThanDistance(int x, int y, int x2, int y2, double length){
        double nonSQRTDLENGHT = length*length;
        if(nonSQRTDLENGHT<getDistancetoNonSQRTD(x,y,x2,y2))return true;
        else return false;
    }
    

    static public   float getDistancetoNonSQRTD(Line line){
        return getDistancetoNonSQRTD(line.start,line.end);
    }

    static public   float getDistancetoNonSQRTD(Point start, Point end){
        return getDistancetoNonSQRTD(start.x, start.y, end.x, end.y);
    }

    static public   float getDistancetoNonSQRTD(int x, int y, int x2, int y2){
        int dx = x - x2;
        int dy = y - y2;
        return dx * dx + dy * dy;
    }


    static public class Line{

        public Point start;
        public Point end;

        public Line(int xStart,int yStart, int xEnd, int yEnd) {
            start = new Point(xStart,yStart);
            end = new Point(xEnd,yEnd);
        }

        public Line(Point Start, Point End){
            start=Start;
            end=End;
        }

    }

    static public Line reverseLine(Line line){
        return new Line(line.end,line.start);
    }

    static public class Intersection{
        public boolean intersects = false;
        public Point intersectionPoint = new Point();
    }

    static public Line getPerpindicularCenterIntersectedLine(Line originalLine){


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
        return new ViewTools.Line(perpLineStartX,perpLineStartY,perpLineEndX,perpLineEndY);
    }

    // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines
    // intersect the intersection point may be stored in the floats i_x and i_y.
    static public Intersection get_line_intersection(Line lineOne, Line lineTwo){

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

    static public Point vectorToPoint(double vectorinRadians, double length, Point start, Rect bounds){
        Point point = vectorToPoint(vectorinRadians, length, start);

        if(!containsInner(point, bounds)){
            if(point.x<=bounds.left) point.x=bounds.left+1;
            if(point.x>=bounds.right) point.x=bounds.right-1;
            if(point.y>=bounds.top) point.y=bounds.top+1;
            if(point.y<=bounds.bottom) point.y=bounds.bottom-1;
        }


        return point;
    }



    static public PointF vectorToFloatPoint(double vectorinRadians, double length, PointF start){
        PointF point = vectorToFloatPoint(vectorinRadians, length);

        point.x+=start.x;
        point.y+=start.y;

        return point;
    }

    static public PointF vectorToFloatPoint(double vectorinRadians, double length){
        PointF point = new PointF();

        point.x=  (float)(length*Math.cos(vectorinRadians));
        point.y= (float) (length*Math.sin(vectorinRadians));

        return point;
    }

    static public Point vectorToPoint(double vectorinRadians, double length){
        Point point = new Point();

        point.x= (int) (length*Math.cos(vectorinRadians));
        point.y= (int) (length*Math.sin(vectorinRadians));

        return point;
    }


    static public Point vectorToPoint(double vectorinRadians, double length, Point start){
        Point point = vectorToPoint(vectorinRadians, length);

        point.x+=start.x;
        point.y+=start.y;

        return point;
    }

    static public double getAngleDotProduct(Point p1, Point p2, Point p3){
        float x1 = p1.x;
        float x2 = p2.x;
        float x3 = p3.x;
        float x4 = p2.x;

        float y1 = p1.y;
        float y2 = p2.y;
        float y3 = p3.y;
        float y4 = p2.y;

        float dx1 = x2-x1;
        float dy1 = y2-y1;
        float dx2 = x4-x3;
        float dy2 = y4-y3;

        float d = dx1*dx2 + dy1*dy2;   // dot product of the 2 vectors
        float l2 = (dx1*dx1+dy1*dy1)*(dx2*dx2+dy2*dy2); // product of the squared lengths

        return Math.toDegrees(Math.acos(d / Math.sqrt(l2)));
    }

    static public double getHypotenuse(double side1, double side2){

        return Math.sqrt((side1*side1) + (side2*side2));

    }

    public static double getArcTan2Mapped(Point start, Point end){

        /*Double radian = Math.atan2(end.y-start.y,end.x-start.x);
        if(radian<0)radian+=FULLCIRCLE;
        return radian;*/
        return getArcTan2Mapped(end.y-start.y,end.x-start.x);
    }

    public static double getArcTan2Mapped(double y, double x){

        Double radian = Math.atan2(y,x);

        if(radian<0)radian+=FULLCIRCLE;

        return radian;
    }

    public static Rect marginRectPercent(Rect rect, float percent){
        if(percent<=0||percent>1)return new Rect(0,0,0,0);

        Rect smallerRect=new Rect(rect);
        float ydelta = (smallerRect.height()*percent)/2;
        float xdelta = (smallerRect.width()*percent)/2;
        smallerRect.inset((int)xdelta,(int)ydelta);


        return smallerRect;
    }

    public static Point getWindowSize(Context context){

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Rect getWindowBounds(Context context){
        Point size = getWindowSize(context);
        return new Rect(0,0,size.x,size.y);
    }

    public static boolean containsInner(Point point, Rect bounds){
        return containsInner(point.x, point.y, bounds);
    }

    public static boolean containsInner(float x, float y, Rect bounds) {
        return bounds.left < x && x < bounds.right && bounds.bottom > y && y > bounds.top;
    }

    public static Point getRandomPoint(Rect Bounds){
        Random randomGen = new Random();
        Point thisPoint = new Point();
        thisPoint.x = randomGen.nextInt(Bounds.width())+Bounds.left;
        thisPoint.y = randomGen.nextInt(Bounds.height())+Bounds.top;
        return thisPoint;

    }

    public static Line[] rectToLines(Rect rect){

        Point northWest = new Point(rect.left,rect.bottom);
        Point northEast = new Point(rect.right,rect.bottom);
        Point southEast = new Point(rect.right,rect.top);
        Point southWest = new Point(rect.left,rect.top);

        return new Line[]{
                new Line(northWest,northEast),
                new Line(northEast,southEast),
                new Line(southEast,southWest),
                new Line(southWest,northWest)
        };
    }

    public static Line[] rectToLines(Rect rect, Path srcPath){
        Line[] borderLines = rectToLines(rect);

        srcPath.addPath(linesToPath(borderLines));

        return borderLines;
    }

    public static Line[] rectToLinesCCW(Rect rect){
        Line[] borderLines = rectToLines(rect);
        Line[] CCWLines = new Line[4];

        for(int i=0;i<4;i++){
            CCWLines[3-i]=reverseLine(borderLines[i]);
        }

        return CCWLines;
    }

    private static PathPlus linesToPath(Line[] lines) {
        PathPlus pathPlus = new PathPlus();
        for(int i=0;i<lines.length;i++)pathPlus.makeLine(lines[i]);
        return pathPlus;
    }
    private static PathPlus linesToPath(Point[] points) {
        PathPlus pathPlus = new PathPlus();
        for(int i=0;i<points.length-1;i++)pathPlus.makeLine(points[i],points[i+1]);
        return pathPlus;
    }



}
