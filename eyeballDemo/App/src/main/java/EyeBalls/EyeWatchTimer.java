package EyeBalls;

import android.animation.ValueAnimator;
import android.view.View;


/**
 * Created by Brandon on 12/9/13.
 */

//for use by Implementations of ABS BRAIN & Itterator

public class EyeWatchTimer {

    private static final long JUST_UNDER_FPS = 33;


    private View mWathMeView;
    private EyeBrain mSmartEyes;
    private EyeFocusListBroadcasterABS mStupidEyes;
    private EyeFocusBrainInterface.EyeBrainID mID = new EyeFocusBrainInterface.EyeBrainID(EyeFocusBrainInterface.EyeBrainID.NO_ID);


    public EyeWatchTimer(View view, EyeFocusListBroadcasterABS messageReciever, int index) {
        makeTimer(view, messageReciever, index);
    }
    public EyeWatchTimer(View view, EyeBrain messageReciever) {
        makeTimer(view, messageReciever, mID );
    }
    public EyeWatchTimer(View view, EyeBrain messageReciever, EyeFocusBrainInterface.EyeBrainID id) {

        makeTimer(view, messageReciever, id);

    }

    public void makeTimer(final View watchMeView, final EyeFocusListBroadcasterABS messageReciever, final int index){

        ValueAnimator watchTimer = ValueAnimator.ofFloat(0,1);
        watchTimer.setDuration(300);
        watchTimer.setRepeatCount(ValueAnimator.INFINITE);
        watchTimer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(!messageReciever.isFocused(index)){

                    animation.cancel();
                }else{


                    messageReciever.focusHere((int) watchMeView.getX(),(int) watchMeView.getY());

                }
            }
        });
        watchTimer.start();


//        mTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if(!messageReciever.isFocused(index)){
//                    Log.d("Timer", "timer canceled");
//                    mTimer.cancel();
//                }else{
//                    Log.d("Timer", "timer sending messages");
//
//                    messageReciever.focusHere((int) watchMeView.getX(),(int) watchMeView.getY());
//
//                }
//
//
//            }
//        },0,JUST_UNDER_FPS);

    }

    public void makeTimer(final View watchMeView, final EyeBrain messageReciever, final EyeFocusBrainInterface.EyeBrainID eyeID){


        ValueAnimator watchTimer = ValueAnimator.ofFloat(0,1);
        watchTimer.setDuration(300);
        watchTimer.setRepeatCount(ValueAnimator.INFINITE);
        watchTimer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {


                   EyeFocusBrainInterface.EyeBrainID response= messageReciever.thinkFocusHere((int) watchMeView.getX(), (int) watchMeView.getY(), eyeID);

                    if(response.Response== EyeFocusBrainInterface.EyeBrainID.NO_LONGER_FOCUSED)animation.cancel();


            }
        });
        watchTimer.start();
//
//
//          mTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if(!messageReciever.isFocused()){
//                    Log.d("Timer", "timer canceled");
//                    mTimer.cancel();
//                }else{
//                    Log.d("Timer", "timer sending messages");
//
//
//
//                    messageReciever.thinkRequest((int) watchMeView.getX(),(int) watchMeView.getY(),eyeID);
//                }
//
//
//            }
//        },0,JUST_UNDER_FPS);

    }



}
