package EyeBalls;

import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Brandon on 12/8/13.
 */
public abstract class EyeFocusListBroadcasterABS implements EyeFocus {

    private ArrayList<EyeFocus> mEyeSets = new ArrayList<EyeFocus>();

    ArrayList<Boolean> IS_FOCUSED = new ArrayList<Boolean>();

    void setIsFocused(boolean focused, int index){
        //IS_FOCUSED=focused;
        if(index>mEyeSets.size()-1){
            //inside index of eyes, check if focus array includes it
            if(index>IS_FOCUSED.size()-1){
                for(int x=0;x<IS_FOCUSED.size();x++){
                    IS_FOCUSED.add(false);
                }

            }
            IS_FOCUSED.add(index,focused);
        }
    }

    boolean isFocused(int index){
        if(index>mEyeSets.size()-1){
            //inside index of eyes, check if focus array includes it
            if(index>IS_FOCUSED.size()-1){
                for(int x=0;x<IS_FOCUSED.size();x++){
                    IS_FOCUSED.add(false);
                }

            }
            return IS_FOCUSED.get(index);
        }
        return false;
    }

    public EyeFocus sendMessage(int index){
        return mEyeSets.get(index);
    }
    public void setEyeFocusList(ArrayList<EyeFocus> eyeSets){
        mEyeSets=eyeSets;
    }

    protected EyeFocusListBroadcasterABS(){}

    protected EyeFocusListBroadcasterABS(ArrayList<EyeFocus> eyeFocus) {
        mEyeSets=eyeFocus;
    }

    public void addEyeFocustoBrain(EyeFocus eyeFocus){
        mEyeSets.add(eyeFocus);
    }


    @Override
    public void request(int RequestCode) {
        for(int i=0;i<mEyeSets.size();i++)mEyeSets.get(i).request(RequestCode);

    }

    @Override
    public void focusHere(View view) {
        for(int i=0;i<mEyeSets.size();i++)mEyeSets.get(i).focusHere(view);
    }

    @Override
    public void focusHere(int focusRequestX, int focusRequestY) {
        for(int i=0;i<mEyeSets.size();i++)mEyeSets.get(i).focusHere(focusRequestX,focusRequestY);
    }

    @Override
    public void focusHere(int focusRequestX, int focusRequestY, int index) {
        if(index<mEyeSets.size())mEyeSets.get(index).focusHere(focusRequestX,focusRequestY);
    }

    @Override
    public int getIndexofClosest(int x, int y) {

        Point[] closestEyes = new Point[mEyeSets.size()];
        for(int i=0;i<mEyeSets.size();i++){
           closestEyes[i]=mEyeSets.get(i).getCenterinWindow(mEyeSets.get(i).getIndexofClosest(x,y));
        }
        return EyeSet.getIndexofClosest(x,y,closestEyes);
    }

    @Override
    public int getmNumEyesets() {
        return mEyeSets.size();
    }

    /**
     * returns null for now
     * @param index
     * @return
     */
    @Override
    public Point getCenterinWindow(int index) {
        for(int i=0;i<mEyeSets.size();i++){
            mEyeSets.get(index).getCenterinWindow(i);
            //logic to be done here
            // todo decide how to return a single point for center in window of an eyeset,
        }
        return null;
    }
}
