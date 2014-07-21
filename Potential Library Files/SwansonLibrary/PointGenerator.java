package SwansonLibrary;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Brandon on 4/30/14.
 */
public class PointGenerator {

    private final Rect mBounds;

    private class RadianRange{

        public double begining;
        public double end;

        private RadianRange(double begining, double end) {
            this.begining = begining;
            this.end = end;

        }
        private double range(){
            return end-begining;
        }
    }



    private Random randomGen = new Random();

    ///DATA STRUCTURE
    //private Point mLinePoints[];
    private int mMaxLength;
    private int mMinLength;

    private ViewTools.Line BorderLines[];
    private float mMinAngle;



    public PointGenerator(int mMaxLength, int mMinLength, Rect bounds, float minAngle) {
        this.mMaxLength = mMaxLength;
        this.mMinLength = mMinLength;
        this.mMinAngle=minAngle;


        mBounds = bounds;

        BorderLines = ViewTools.rectToLines(bounds);



    }



    public RadianExclusionMap makeMap(Point Origin, Point CurrentPoint){
        return new RadianExclusionMap(Origin,CurrentPoint);
    }

    public class RadianExclusionMap{
        private ArrayList<RadianRange> mExcludedRanges = new ArrayList<RadianRange>();
        private Point mCurrentPoint;
        private double mAvailableRadians = ViewTools.FULLCIRCLE;
        private double mTotalExludedRadians = 0;

        private RadianExclusionMap(Point Origin, Point CurrentPoint) {

            mCurrentPoint=CurrentPoint;

            //opposite angle exclusion
            Double lastRadian = ViewTools.getArcTan2Mapped(Origin,CurrentPoint);
            double beginingRad; double endRad;
            beginingRad = (lastRadian + ViewTools.FULLCIRCLE - (mMinAngle*Math.PI) + Math.PI)%ViewTools.FULLCIRCLE;
            endRad = (lastRadian + (mMinAngle*Math.PI) + Math.PI)%ViewTools.FULLCIRCLE;


            if(beginingRad>endRad){
                mExcludedRanges.add(new RadianRange(beginingRad,ViewTools.FULLCIRCLE));
                beginingRad=0;
            }
            mExcludedRanges.add(new RadianRange(beginingRad,endRad));


            //detect proximity to walls, exclude them

            if(mCurrentPoint.y-mBounds.top<mMinLength){

                mExcludedRanges.add(getWallRadianRange(mBounds.top, ViewTools.RadianAngles.TOP, mCurrentPoint.y, true));
            }

            if(mBounds.bottom-mCurrentPoint.y<mMinLength){
                mExcludedRanges.add(getWallRadianRange(mBounds.bottom, ViewTools.RadianAngles.BOTTOM,mCurrentPoint.y,true));
            }
            if(mCurrentPoint.x-mBounds.left<mMinLength){
                mExcludedRanges.add(getWallRadianRange(mBounds.left, ViewTools.RadianAngles.LEFT,mCurrentPoint.x,false));
            }
            if(mBounds.right-mCurrentPoint.x<mMinLength){
                mExcludedRanges.add(getWallRadianRange(mBounds.right, ViewTools.RadianAngles.RIGHT,mCurrentPoint.x,false));
            }

            //sort and merge
            if(mExcludedRanges.size()>1){
                mExcludedRanges=SortMergeImroved();
            }

            //calc available
            for(int i=0;i<mExcludedRanges.size();i++){
                mTotalExludedRadians+=mExcludedRanges.get(i).range();
                mAvailableRadians-=mExcludedRanges.get(i).range();
            }



        }



        private ArrayList<RadianRange> SortMergeImroved(){
            ArrayList<RadianRange> unSortedRanges = (ArrayList<RadianRange>) mExcludedRanges.clone();
            ArrayList<RadianRange> sortedRanges = new ArrayList<RadianRange>();
            ArrayList<RadianRange> mergedRanges = new ArrayList<RadianRange>();


            //sort
            while (unSortedRanges.size()>0){

                RadianRange smallestRadian=unSortedRanges.get(0);
                for(int v=0;v<unSortedRanges.size();v++){
                    if(unSortedRanges.get(v).begining<smallestRadian.begining) {
                        smallestRadian=unSortedRanges.get(v);
                    }
                }
                sortedRanges.add(smallestRadian);
                unSortedRanges.remove(smallestRadian);

            }

            //merge
            for(int i=0;i<sortedRanges.size()-1;i++){
                if(sortedRanges.get(i).end>sortedRanges.get(i+1).begining){
                    sortedRanges.get(i+1).begining=sortedRanges.get(i).begining;
                }else mergedRanges.add(sortedRanges.get(i));
            }
            mergedRanges.add(sortedRanges.get(sortedRanges.size()-1));

            return (ArrayList<RadianRange>)mergedRanges.clone();

        }

        private RadianRange getWallRadianRange(int walledge, double wallRadian, int point, boolean y) {


            int opposite = point-walledge;
            Double adjecent = Math.sqrt((mMinLength*mMinLength-opposite*opposite));
            double rad;
            if(y)rad=ViewTools.getArcTan2Mapped(opposite, adjecent);
            else rad=ViewTools.getArcTan2Mapped(adjecent, opposite);

            double wallRadStart = (rad+Math.PI)%ViewTools.FULLCIRCLE;;
            double wallRadEnd = (wallRadian + (wallRadian-wallRadStart)+ ViewTools.FULLCIRCLE)%ViewTools.FULLCIRCLE;



            if(wallRadEnd<wallRadStart && wallRadian!=0){
                double radtemp=wallRadStart;
                wallRadStart=wallRadEnd;
                wallRadEnd=radtemp;
            }

            if(wallRadian==0){
                //mAvailableRadians-=(ViewTools.FULLCIRCLE-wallRadStart);

                mExcludedRanges.add(new RadianRange(wallRadStart,ViewTools.FULLCIRCLE)); //find a much better way for this!

                wallRadStart=0;
            }



            return new RadianRange(wallRadStart,wallRadEnd);

        }



        public Point getPoint(){

            double approvedRad=getAppropriateRad();
            double approvedLength=getAppropriateLenth(approvedRad);


            Point generatedPoint = ViewTools.vectorToPoint(approvedRad, approvedLength, mCurrentPoint,mBounds);

            /*Log.d("PATH","generated point=" + generatedPoint.x + "/" + generatedPoint.y  + " from:" +
                    mCurrentPoint.x + "/" + mCurrentPoint.y);
            Log.d("PATH", "VECTOR:"+approvedRad + "  LENGHT:" + approvedLength);*/

            /*if(generatedPoint.x==mBounds.left)generatedPoint.x=generatedPoint.x+3;
            if(generatedPoint.x==mBounds.right)generatedPoint.x=generatedPoint.x-3;
            if(generatedPoint.y==mBounds.top)generatedPoint.y-=3;
            if(generatedPoint.y==mBounds.bottom)generatedPoint.y+=3;

            int whilecount=0;
            while (!ViewTools.containsInner(generatedPoint, mBounds)){
                approvedLength-=approvedLength*.005;
                generatedPoint=ViewTools.vectorToPoint(approvedRad, approvedLength, mCurrentPoint);
                whilecount++;
                if(whilecount>100){
                    Log.d("PATH","we are broken in the while");
                    return ViewTools.getRandomPoint(mBounds);

                }

            }*/


            return generatedPoint;
        }




        private double getAppropriateRad() {
            double unmappedRad = mAvailableRadians*randomGen.nextFloat();

            return getMappedRad(unmappedRad);
        }
        private double getMappedRad(double unmappedRad){

            for(int i=0;i<mExcludedRanges.size();i++){
                if(unmappedRad>mExcludedRanges.get(i).begining){
                    unmappedRad+=mExcludedRanges.get(i).range();
                }
            }

            return unmappedRad;
        }


        private double getAppropriateLenth(double approvedRad) {
            //get point off frame
            Point offScreenPoint=ViewTools.vectorToPoint(approvedRad, mMaxLength, mCurrentPoint);
            //find intersecton

            ViewTools.Line extendedLine = new ViewTools.Line(mCurrentPoint.x,mCurrentPoint.y,offScreenPoint.x,offScreenPoint.y);

            ViewTools.Intersection intersections[]= new ViewTools.Intersection[4];

            ArrayList<Point> borderpoints = new ArrayList<Point>();
            Point borderpoint = new Point(-1,-1);

            int numIntersections=0;

            for(int i=0;i<4;i++){
                intersections[i]=ViewTools.get_line_intersection(extendedLine,BorderLines[i]);
                if(intersections[i].intersects) {
                    numIntersections++;
                    borderpoints.add(intersections[i].intersectionPoint);
                }
            }
            if(numIntersections>1){
               double greatestDistance=0;
               for(int i=0;i<borderpoints.size();i++){
                   if(ViewTools.getDistancetoNonSQRTD(mCurrentPoint,borderpoints.get(i))>greatestDistance){
                       borderpoint=borderpoints.get(i);
                   }
               }
            }
            else if(numIntersections==0){
                Log.d("PATH", "border intersection not detected");
            }
            else borderpoint = borderpoints.get(0);



            //use to calc distance
            Double WallLength=Math.sqrt(ViewTools.getDistancetoNonSQRTD(mCurrentPoint.x, mCurrentPoint.y, borderpoint.x, borderpoint.y));


            //get random length


            double pointLength;

            if(WallLength>=mMinLength){
                pointLength=randomGen.nextFloat()*(WallLength-mMinLength)+mMinLength; //for rounding error
            }else{

                pointLength=WallLength;

            }
            if(pointLength==0){
                Log.d("PATH", "zero length");
            }
            return pointLength;
        }

        //fordebug///
        public ArrayList<Point> getAllPoints(){
            ArrayList<Point> generatedPoints = new ArrayList<Point>();

            for(float v = 0f;v<1;v+=.005){
                double unmappedRad=mAvailableRadians*v;
                double mappedRad = getMappedRad(unmappedRad);
                generatedPoints.add(ViewTools.vectorToPoint(mappedRad, mMinLength, mCurrentPoint));
            }
            return generatedPoints;
        }

        public void Visualize(Path path, TextView readout){

            //readout.setText(" ");

            path.rewind();
            for(int i=0;i<mExcludedRanges.size();i++){
                RadianRange range = mExcludedRanges.get(i);
                Point drawPoint = ViewTools.vectorToPoint(range.begining, mMinLength, mCurrentPoint);
                path.moveTo(mCurrentPoint.x,mCurrentPoint.y);path.lineTo(drawPoint.x,drawPoint.y);
                drawPoint = ViewTools.vectorToPoint(range.end, mMinLength, mCurrentPoint);
                path.moveTo(mCurrentPoint.x,mCurrentPoint.y);path.lineTo(drawPoint.x,drawPoint.y);



                for(double lineRadian=range.begining;lineRadian<range.end;lineRadian+=Math.PI/90){
                    //line from newpoint to radian vector, 1/3 min length
                    drawPoint=ViewTools.vectorToPoint(lineRadian, mMinLength * .6f, mCurrentPoint);
                    path.moveTo(mCurrentPoint.x,mCurrentPoint.y);
                    path.lineTo(drawPoint.x,drawPoint.y);
                }

                DecimalFormat oneDigit = new DecimalFormat("#.#");


                readout.setText(readout.getText()+" EX"+ i + "="+
                        oneDigit.format(range.begining) + "-" + oneDigit.format(range.end));

            }

            DecimalFormat twoDigit = new DecimalFormat("#.##");
            readout.setText(readout.getText()+"AVIL:" + twoDigit.format(mAvailableRadians)+
            "EX:"+ twoDigit.format(mTotalExludedRadians) + "  " + twoDigit.format(mTotalExludedRadians+mAvailableRadians));

            if(mBounds.bottom-mCurrentPoint.y<mMinLength){
                Point drawpoint  = ViewTools.vectorToPoint(
                        ViewTools.RadianAngles.BOTTOM,
                        mBounds.bottom-mCurrentPoint.y, mCurrentPoint);
                path.addCircle(drawpoint.x,drawpoint.y,10, Path.Direction.CCW);

                //1.5 pi

            }
            if(mCurrentPoint.y-mBounds.top<mMinLength){

                Point drawpoint  = ViewTools.vectorToPoint(
                        ViewTools.RadianAngles.TOP,
                        mCurrentPoint.y-mBounds.top, mCurrentPoint);
                path.addCircle(drawpoint.x,drawpoint.y,10, Path.Direction.CCW);

                //half pi

            }
            if(mCurrentPoint.x-mBounds.left<mMinLength){

                Point drawpoint  = ViewTools.vectorToPoint(
                        ViewTools.RadianAngles.LEFT,
                        mCurrentPoint.x - mBounds.left, mCurrentPoint);
                path.addCircle(drawpoint.x,drawpoint.y,10, Path.Direction.CCW);

                //1pi

            }
            if(mBounds.right-mCurrentPoint.x<mMinLength){

                Point drawpoint  = ViewTools.vectorToPoint(
                        ViewTools.RadianAngles.RIGHT,
                        mBounds.right - mCurrentPoint.x, mCurrentPoint);
                path.addCircle(drawpoint.x,drawpoint.y,10, Path.Direction.CCW);

                //0pi

            }

        }
    }



}
