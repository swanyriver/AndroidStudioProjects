package EyeBalls;

import android.view.View;

/**
 * Created by Brandon on 12/7/13.
 */
public class EyeBrainAdapter extends EyeBrain {
    public EyeBrainAdapter(EyeFocus eyeFocus) {
        super(eyeFocus);
    }

    @Override
    public EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, int Index, EyeBrainID requestID) {
        //return thinks nothing
        //sendMessage().focusHere(focusRequestX,focusRequestY,Index);
        focusHere(focusRequestX,focusRequestY,Index);
        //Log.d(EyeFocusBrainInterface.TAG, EyeFocusBrainInterface.NO_THOUGHTS_LOG);
        return EyeFocusBrainInterface.NO_THOUGHTS;
    }

    @Override
    public EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, EyeBrainID requestID) {
        //return thinks nothing
        //sendMessage().focusHere(focusRequestX,focusRequestY);
        focusHere(focusRequestX,focusRequestY);
        //Log.d(EyeFocusBrainInterface.TAG, EyeFocusBrainInterface.NO_THOUGHTS_LOG);
        return EyeFocusBrainInterface.NO_THOUGHTS;
    }


    @Override
    public EyeBrainID thinkFocusHere(View view, EyeBrainID requestID) {
        //return thinks nothing
        //sendMessage().focusHere(view);
        focusHere(view);
        //Log.d(EyeFocusBrainInterface.TAG, EyeFocusBrainInterface.NO_THOUGHTS_LOG);
        return EyeFocusBrainInterface.NO_THOUGHTS;

    }

    @Override
    public EyeBrainID thinkRequest(int requestCode, EyeBrainID requestID) {
        //sendMessage().thinkRequest(requestCode);
        request(requestCode);
        //Log.d(EyeFocusBrainInterface.TAG, EyeFocusBrainInterface.NO_THOUGHTS_LOG);
        return EyeFocusBrainInterface.NO_THOUGHTS;
    }
}
