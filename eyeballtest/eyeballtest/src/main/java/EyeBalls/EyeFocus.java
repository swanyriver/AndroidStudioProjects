package EyeBalls;

import android.graphics.Point;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Brandon on 12/1/13.
 */

public interface EyeFocus{


    void request(int RequestCode);
    //void thinkRequest(int RequestCode, int index);

    //todo implement thinkRequest code index

    void focusHere(final View view);
    void focusHere(int focusRequestX, int focusRequestY);
    void focusHere(int focusRequestX, int focusRequestY,int index);
    int getIndexofClosest(int x, int y);
    int getmNumEyesets();
    Point getCenterinWindow(int index);
    // getCenterInWindow();   ////////add implementation to SET, Group, Brain, BrainAdapter
}


//put getters in here, center in window?
/*  public int getPupilCenterX(){
        return mPupilCenterX;
    }

    public int getPupilCenterY(){
        return mPupilCenterY;
    }

    public int getmEyeballCenterWindowX() {return mEyeballCenterWindowX; }

    public int getmEyeballCenterWindowY() { return mEyeballCenterWindowY; }*/