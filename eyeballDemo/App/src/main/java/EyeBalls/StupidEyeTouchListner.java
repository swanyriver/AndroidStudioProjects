package EyeBalls;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Brandon on 12/15/13.
 */
public class StupidEyeTouchListner implements View.OnTouchListener{

    private int mFinger1id;
    private EyeFocus mEyeFocus;

    public StupidEyeTouchListner(EyeFocus eyeFocus){
        mEyeFocus=eyeFocus;
    }
    public EyeFocus sendStupidTouchMessage(){
        return mEyeFocus;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int indexDown = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                mFinger1id = event.getPointerId(indexDown);

                //for(int x=0;x<mEyeballs.size();x++) mEyeballs.get(x).thinkRequest((int) event.getX(index), (int) event.getY(index));

                //sendtoEyeballsorEyeSet(event,indexDown);
                sendStupidTouchMessage().focusHere((int) event.getX(), (int) event.getY());


                //Log.d("touch","action  down");


                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                //Log.d("touch","action pointer down");

                break;
            case MotionEvent.ACTION_POINTER_UP:


                sendStupidTouchMessage().request(EyeBall.PLEASE_LOOSE_FOCUS);


                break;
            case MotionEvent.ACTION_UP:

                sendStupidTouchMessage().request(EyeBall.PLEASE_LOOSE_FOCUS);


                break;


            case MotionEvent.ACTION_CANCEL:
                //Log.d("touch","action cancel");
                sendStupidTouchMessage().request(EyeBall.PLEASE_LOOSE_FOCUS);

                break;

            case MotionEvent.ACTION_MOVE:
                // Log.d("touch", "action move");

                //int index = event.findPointerIndex(mFinger1id);


                for(int x=0;x<event.getPointerCount();x++)
                {
                    //if(event.getPointerId(x)==mFinger1id)  for(int i=0;i<mEyeballs.size();i++) mEyeballs.get(i).thinkRequest((int) event.getX(x), (int) event.getY(x));

                    if(event.getPointerId(x)==mFinger1id){

                        sendStupidTouchMessage().focusHere((int) event.getX(), (int) event.getY());

                    }

                }



                break;

            default:
                break;
        }



        return false;
    }


}
