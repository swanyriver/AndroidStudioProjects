package EyeBalls;

import android.graphics.Point;
import android.view.View;

/**
 * Created by Brandon on 12/7/13.
 */
abstract public class EyeBrain implements EyeFocusBrainInterface{

    EyeFocus mEyeGroup;

    boolean IS_FOCUSED = false;

    void setIsFocused(boolean focused){
        IS_FOCUSED=focused;
    }

    boolean isFocused(){
        return IS_FOCUSED;
    }


    public EyeBrain(EyeFocus eyeFocus) {
        mEyeGroup=eyeFocus;
    }

    final public EyeFocus sendMessage(){
        return mEyeGroup;
    }


    @Override
    public void focusHere(int focusRequestX, int focusRequestY) {
        sendMessage().focusHere(focusRequestX,focusRequestY);
    }

    @Override
    public void request(int RequestCode) {
        sendMessage().request(RequestCode);
        if(RequestCode==EyeBall.PLEASE_LOOSE_FOCUS){
            setIsFocused(false);
            sendMessage().request(RequestCode);
        }
    }

    @Override
    public void focusHere(View view) {
        setIsFocused(true);
        new EyeWatchTimer(view,this);
        //sendMessage().focusHere(view);
    }

    @Override
    public void focusHere(int focusRequestX, int focusRequestY, int index) {
        sendMessage().focusHere(focusRequestX,focusRequestY,index);
    }

    @Override
    public int getIndexofClosest(int x, int y) {
       return sendMessage().getIndexofClosest(x,y);
    }

    @Override
    public Point getCenterinWindow(int index) {
        return sendMessage().getCenterinWindow(index);
    }

    @Override
    public int getmNumEyesets() {
        return sendMessage().getmNumEyesets();
    }
}
