package EyeBalls;


import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by Brandon on 12/8/13.
 */
public class EyeGroup extends EyeFocusListBroadcasterABS {





    private ArrayList<EyeSet> mEyeSets = new ArrayList<EyeSet>();


    private int mNumEyesets=0;




    public EyeGroup addEyeSet(FrameLayout drawFrame){

        EyeSet currentEyeset = new EyeSet(drawFrame);

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
