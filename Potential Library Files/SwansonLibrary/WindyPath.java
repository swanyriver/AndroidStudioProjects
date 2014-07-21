package SwansonLibrary;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Brandon on 4/29/14.
 */
public class WindyPath extends Path{

    ////CONTROL VARIABLES
    private int mLineNumber;
    /////////////////////

    ///DEFAULT CONTROL VARIABLES
    protected int mLineNumberDEFAULT=100;
    protected int mMarginDEFAULT=30;
    protected float mMinLengthPercentageDEFAULT=.17F;

    ///FIXED CONTROL VARIABLES
    static public float MINIMUM_ANGLE_DEFAULT = .23f;  //as a percent of 180  .25 = 45degrees = 90 degrees total


    ///DATA STRUCTURE
    protected Point mLinePoints[];
    protected PointGenerator mPointGenerator;
    private Rect mBounds;

    /////UTILITY
    private Random randomGen = new Random();



    //////////////CONSTRUCTOR

    public WindyPath(int Width, int Height) {

        inflateData(mLineNumberDEFAULT,mMarginDEFAULT,Width,Height,mMinLengthPercentageDEFAULT,MINIMUM_ANGLE_DEFAULT);
        generate();
    }
    public WindyPath(Point size){
        inflateData(mLineNumberDEFAULT,mMarginDEFAULT,size.x,size.y,mMinLengthPercentageDEFAULT,MINIMUM_ANGLE_DEFAULT);
        generate();
    }
    public WindyPath(int Width, int Height, int LineNumber, float MinLengthPercentage, int Margin, float minAngle) {

        inflateData(LineNumber,Margin,Width,Height,MinLengthPercentage,minAngle);
        generate();

    }
    public WindyPath(int Width, int Height,int LineNumber ,float MinLengthPercentage, float minAngle,Rect bounds) {
        inflateData(LineNumber,MinLengthPercentage,minAngle,bounds);
        generate();

    }
    public WindyPath(Rect bounds, int LineNumber) {
        inflateData(LineNumber,mMinLengthPercentageDEFAULT,MINIMUM_ANGLE_DEFAULT,bounds);
        generate();

    }

    protected WindyPath() {
    }


    protected void inflateData(int LineNumber, int Margin, int Width, int Height, float MinLengthPercentage, float MinAngle){
        Rect bounds = new Rect(0,0,Width,Height);


        bounds.inset(Margin,Margin);

        inflateData(LineNumber, MinLengthPercentage, MinAngle, bounds);
    }

    protected void inflateData(int LineNumber,float MinLengthPercentage, float MinAngle, Rect bounds) {

        mBounds = bounds;

        mLineNumber=LineNumber;

        int MaxLength = (int) ViewTools.getHypotenuse(bounds.width(),bounds.height());
        int MinLength = (int) (MaxLength*MinLengthPercentage);

        if(MinAngle>MINIMUM_ANGLE_DEFAULT)MinAngle=MINIMUM_ANGLE_DEFAULT;

        mPointGenerator = new PointGenerator(MaxLength,MinLength,bounds,MinAngle);



    }

    public void generate(){
        rewind();

        mLinePoints = new Point[mLineNumber+1];

        mLinePoints[0]=ViewTools.getRandomPoint(mBounds);
        mLinePoints[1]=mPointGenerator.makeMap(mLinePoints[0],mLinePoints[0]).getPoint();
        for(int x=2;x<mLineNumber+1;x++){

            mLinePoints[x]=mPointGenerator.makeMap(mLinePoints[x-2],mLinePoints[x-1]).getPoint();

        }

        makeCurves();

    }

    protected void makeCurves() {
        Point midpoints[] = new Point[mLineNumber];
        for(int x=0;x<mLineNumber;x++)midpoints[x]=getMidPoint(mLinePoints[x], mLinePoints[x+1]);

        moveTo(midpoints[0].x, midpoints[0].y);
        for(int i=1;i<mLineNumber;i++){
            quadTo(mLinePoints[i].x, mLinePoints[i].y, midpoints[i].x, midpoints[i].y);
        }
    }



    ////////////UTILITY FUCTIONS



    private Point getMidPoint(Point start, Point end){
        int dx = end.x - start.x;
        int dy = end.y - start.y;

        int orginalLineMidX = start.x + Math.round(.5f * dx);
        int orginalLineMidY = start.y + Math.round(.5f * dy);

        return new Point(orginalLineMidX,orginalLineMidY);

    }

    ///FOR DEBUG
    public Point[] getLinePoints(){
        return mLinePoints;
    }
    public int getLineNumber() {
        return mLineNumber;
    }
}
