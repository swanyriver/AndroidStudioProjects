package EyeBalls;

import android.graphics.Paint;
import android.graphics.Path;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by Brandon on 12/8/13.
 */
public class EyeGroup extends EyeFocusListBroadcasterABS {

    //for Vizualizations
    public Boolean getVizualizationOn(int index){
        return mEyeSets.get(index).VISUALIZATION_ON;
    }
    public void setVizualizationOn(int index, boolean vizOn){
        mEyeSets.get(index).VISUALIZATION_ON=vizOn;
    }
    public Paint getvizPaint(int index){
        return mEyeSets.get(index).mLinePaint;
    }
    public Path getvizPath(int index){
        return mEyeSets.get(index).mFocusLines;
    }
    public Path getvizBorderPath(int index){
        return mEyeSets.get(index).mBorderLinePath;
    }
    public void resetVizPath(int index) {
        mEyeSets.get(index).mFocusLines.reset();
    }
    //for vizualizations



    private ArrayList<EyeSet> mEyeSets = new ArrayList<EyeSet>();

    //private ArrayList<EyeFocus> mEyeFocus = new ArrayList<EyeFocus>();
    private int mNumEyesets=0;




    public EyeGroup addEyeSet(FrameLayout drawFrame){

        EyeSet currentEyeset = new EyeSet(drawFrame);
        //mEyeFocus.add(currentEyeset);
        mEyeSets.add(currentEyeset);
        mNumEyesets++;
        addEyeFocustoBrain(currentEyeset);

        return this;

    }

    public EyeGroup addEyeset(EyeSet eyeset){

        mEyeSets.add(eyeset);
        mNumEyesets++;
        addEyeFocustoBrain(eyeset);

        return this;
    }

    /**
     * adds a new eye, if group has no sets, initializes new set and add eye to it
     * @param eyeFrame
     * @param height
     * @param width
     * @param localx
     * @param localy
     * @param eyeDraw
     */
    public EyeGroup addEyeball(FrameLayout eyeFrame,int height,int width,float localx,float localy, EyeDraw eyeDraw){
        if(mNumEyesets==0){
            //add to new set
            addEyeSet(eyeFrame).addEyeball(eyeFrame,height,width,localx,localy,eyeDraw);

        }else{
            //add to most recent set
            mEyeSets.get(mNumEyesets-1).addEyeball(height,width,localx,localy,eyeDraw);
        }
        return this;
    }

}
