package EyeBalls;

import android.view.View;

/**
 * Created by Brandon on 12/6/13.
 */
public class ProximityBrain extends EyeBrainAdapter{

    final EyeBrainID Animation = new EyeBrainID(101);

    private int mClosest=-1;

    public ProximityBrain(EyeFocus eyeFocus) {
        super(eyeFocus);
    }

    @Override
    public void focusHere(int focusRequestX, int focusRequestY) {


        int closest = sendMessage().getIndexofClosest(focusRequestX,focusRequestY);
        sendMessage().focusHere(focusRequestX, focusRequestY, closest);
        //todo need to add loosefocus thinkRequest when index changes
        if(closest != mClosest){
            sendMessage().request(EyeBall.PLEASE_LOOSE_FOCUS);
            mClosest=closest;
            //todo use requestcode(index)
        }
    }

    @Override
    public void focusHere(View view) {
        setIsFocused(true);
        new EyeWatchTimer(view,this,Animation);
    }

    @Override
    public EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, EyeBrainID requestID) {

        return super.thinkFocusHere(focusRequestX, focusRequestY, requestID);
    }
}
